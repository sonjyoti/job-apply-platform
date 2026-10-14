
import os
import time
from datetime import datetime, timezone
from html.parser import HTMLParser

import requests


SOURCE_API_URL = "https://remotive.com/api/remote-jobs"
BACKEND_API_URL = os.getenv(
    "BACKEND_API_URL",
    "http://localhost:8080/api/jobs",
)

SEARCH_TERMS = [
    "Java",
    "Spring Boot",
    "Backend Developer",
    "Software Engineer",
]

RELEVANT_TITLE_TERMS = [
    "java developer",
    "java engineer",
    "spring boot",
    "backend developer",
    "backend engineer",
    "back-end developer",
    "back-end engineer",
    "software engineer",
    "software developer",
]

EXCLUDED_TITLE_TERMS = [
    "senior",
    "sr.",
    "lead ",
    "principal",
    "staff engineer",
    "engineering manager",
    "director",
    "architect",
    "freelance writer",
    "copywriter",
    "sales",
    "shopify",
]

ALLOWED_LOCATION_TERMS = [
    "india",
    "worldwide",
    "anywhere",
    "asia-pacific",
    "asia pacific",
    "apac",
]

REQUEST_TIMEOUT = 30
REQUEST_DELAY_SECONDS = 0.5


class HTMLTextExtractor(HTMLParser):
    def __init__(self):
        super().__init__()
        self.parts = []

    def handle_data(self, data):
        text = data.strip()
        if text:
            self.parts.append(text)

    def get_text(self):
        return " ".join(self.parts)

def is_relevant_job(title):
    title = (title or "").strip().lower()

    if not title:
        return False

    if any(term in title for term in EXCLUDED_TITLE_TERMS):
        return False

    return any(term in title for term in RELEVANT_TITLE_TERMS)


def clean_description(html):
    parser = HTMLTextExtractor()
    parser.feed(html or "")
    return parser.get_text()


def accepts_applicants_from_india(location):
    location = (location or "").strip().lower()

    if not location:
        return False

    return any(
        term in location
        for term in ALLOWED_LOCATION_TERMS
    )


def parse_posted_at(value):
    if not value:
        return None

    try:
        parsed = datetime.fromisoformat(
            value.replace("Z", "+00:00")
        )

        # Convert aware timestamps to UTC, then remove timezone
        # because the current Java DTO uses LocalDateTime.
        if parsed.tzinfo is not None:
            parsed = parsed.astimezone(timezone.utc)
            parsed = parsed.replace(tzinfo=None)

        return parsed.isoformat()

    except (TypeError, ValueError):
        return None


def fetch_jobs(session, search_term):
    response = session.get(
        SOURCE_API_URL,
        params={
            "category": "software-dev",
            "search": search_term,
            "limit": 100,
        },
        timeout=REQUEST_TIMEOUT,
    )
    response.raise_for_status()

    payload = response.json()
    jobs = payload.get("jobs", [])

    if not isinstance(jobs, list):
        raise ValueError("Unexpected API response")

    return jobs


def map_job(raw_job):
    candidate_location = raw_job.get(
        "candidate_required_location", ""
    )

    return {
        "externalJobId": str(raw_job["id"]),
        "source": "REMOTIVE",
        "title": raw_job.get("title") or "Untitled job",
        "company": raw_job.get("company_name") or "Unknown company",
        "location": candidate_location or "Not specified",
        "workMode": "REMOTE",
        "description": clean_description(
            raw_job.get("description", "")
        ),
        "salaryMin": None,
        "salaryMax": None,
        "salaryCurrency": "INR",
        "minExperience": None,
        "maxExperience": None,
        "jobUrl": raw_job.get("url"),
        "postedAt": parse_posted_at(
            raw_job.get("publication_date")
        ),
    }


def submit_job(session, job):
    response = session.post(
        BACKEND_API_URL,
        json=job,
        timeout=REQUEST_TIMEOUT,
    )

    if response.status_code == 409:
        return "duplicate"

    if response.status_code == 201:
        return "created"

    response.raise_for_status()
    return "unexpected"


def main():
    jobs_by_id = {}

    created = 0
    duplicates = 0
    failed = 0
    skipped = 0

    with requests.Session() as session:
        session.headers.update({
            "User-Agent": "JobApplyPlatform/0.1",
            "Accept": "application/json",
        })

        # Fetch and deduplicate results before importing.
        for term in SEARCH_TERMS:
            print(f"Searching for: {term}")

            try:
                jobs = fetch_jobs(session, term)

                for job in jobs:
                    job_id = job.get("id")

                    if job_id is not None:
                        jobs_by_id[str(job_id)] = job

            except (requests.RequestException, ValueError) as error:
                print(f"Search failed for '{term}': {error}")

            time.sleep(REQUEST_DELAY_SECONDS)

        print(f"\nUnique listings fetched: {len(jobs_by_id)}")

        for raw_job in jobs_by_id.values():
            title = raw_job.get("title", "")
            location = raw_job.get(
                "candidate_required_location", ""
            )

            if not is_relevant_job(title):
                skipped += 1
                continue

            if not accepts_applicants_from_india(location):
                skipped += 1
                continue

            if not raw_job.get("url"):
                skipped += 1
                continue

            job = map_job(raw_job)

            try:
                result = submit_job(session, job)

                if result == "created":
                    created += 1
                    print(f"Saved: {job['title']} — {job['company']}")

                elif result == "duplicate":
                    duplicates += 1
                    print(f"Already saved: {job['title']}")

                else:
                    failed += 1

            except requests.RequestException as error:
                failed += 1
                print(f"Import failed for '{job['title']}': {error}")

            time.sleep(REQUEST_DELAY_SECONDS)

    print("\nImport summary")
    print(f"New jobs:       {created}")
    print(f"Duplicates:     {duplicates}")
    print(f"Skipped:        {skipped}")
    print(f"Failed:         {failed}")


if __name__ == "__main__":
    main()
