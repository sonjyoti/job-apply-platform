import { useCallback, useEffect, useState } from "react";
import {
  BriefcaseBusiness,
  MapPin,
  ExternalLink,
  RefreshCw,
  Search,
  SlidersHorizontal,
  CheckCircle2,
  AlertCircle,
  GraduationCap,
  Code2,
  UserRound,
} from "lucide-react";
import { getJobMatches } from "./api";
import "./App.css";
import ProfilePage from "./ProfilePage";


function formatScore(score) {
  return typeof score === "number" ? `${Math.round(score)}%` : "N/A";
}

function JobCard({ job }) {
  const score = Number(job.matchScore ?? 0);
  const matchedSkills = job.matchedSkills ?? [];
  const requiredSkills = job.requiredSkills ?? [];
  const preferredSkills = job.preferredSkills ?? [];
  const missingSkills = job.missingRequiredSkills ?? [];
  const reasons = job.reasons ?? [];

  return (
    <article className="job-card">
      <div className="job-card-top">
        <div className="company-icon">
          <BriefcaseBusiness size={23} />
        </div>

        <div className="job-heading">
          <h2>{job.title || "Untitled position"}</h2>
          <p className="company-name">{job.company || "Unknown company"}</p>
        </div>

        <div className="match-badge">
          <strong>{formatScore(job.matchScore)}</strong>
          <span>match</span>
        </div>
      </div>

      <div className="job-meta">
        <span>
          <MapPin size={15} /> {job.location || "Location not specified"}
        </span>
        {job.experienceFit && (
          <span className={`fit-badge ${job.experienceFit.toLowerCase()}`}>
            {job.experienceFit.replaceAll("_", " ")}
          </span>
        )}
      </div>

      <div className="score-section">
        <div className="score-label">
          <span>Overall match</span>
          <strong>{formatScore(job.matchScore)}</strong>
        </div>
        <div className="progress-track">
          <div
            className="progress-fill"
            style={{ width: `${Math.min(100, Math.max(0, score))}%` }}
          />
        </div>
      </div>

      <div className="skill-section">
        <h3>
          <CheckCircle2 size={16} /> Matched skills
        </h3>
        {matchedSkills.length ? (
          <div className="skill-list">
            {matchedSkills.map((skill) => (
              <span className="skill-chip matched" key={skill}>
                {skill}
              </span>
            ))}
          </div>
        ) : (
          <p className="muted">No matching skills identified.</p>
        )}
      </div>

      {requiredSkills.length > 0 && (
        <div className="skill-section">
          <h3>
            <Code2 size={16} /> Required skills
          </h3>
          <div className="skill-list">
            {requiredSkills.map((skill) => (
              <span className="skill-chip" key={skill}>
                {skill}
              </span>
            ))}
          </div>
        </div>
      )}

      {preferredSkills.length > 0 && (
        <div className="skill-section">
          <h3>Preferred skills</h3>
          <div className="skill-list">
            {preferredSkills.map((skill) => (
              <span className="skill-chip preferred" key={skill}>
                {skill}
              </span>
            ))}
          </div>
        </div>
      )}

      {missingSkills.length > 0 && (
        <div className="missing-section">
          <h3>
            <AlertCircle size={16} /> Missing required skills
          </h3>
          <div className="skill-list">
            {missingSkills.map((skill) => (
              <span className="skill-chip missing" key={skill}>
                {skill}
              </span>
            ))}
          </div>
        </div>
      )}

      {job.requiredSkillsCoverage != null && (
        <p className="coverage-text">
          Required skills coverage: {formatScore(job.requiredSkillsCoverage)}
        </p>
      )}

      {reasons.length > 0 && (
        <div className="reason-section">
          <h3>Why this job matches</h3>
          <ul>
            {reasons.map((reason, index) => (
              <li key={`${reason}-${index}`}>{reason}</li>
            ))}
          </ul>
        </div>
      )}

      <div className="job-card-footer">
        <span className="missing-count">
          {job.missingRequiredSkillsCount ?? missingSkills.length} missing
          required skill(s)
        </span>

        {job.jobUrl ? (
          <a
            className="apply-button"
            href={job.jobUrl}
            target="_blank"
            rel="noopener noreferrer"
          >
            View job <ExternalLink size={15} />
          </a>
        ) : (
          <span className="muted">No job link available</span>
        )}
      </div>
    </article>
  );
}

export default function App() {
  const [jobs, setJobs] = useState([]);
  const [minScore, setMinScore] = useState(0);
  const [maxMissing, setMaxMissing] = useState("");
  const [fresherOnly, setFresherOnly] = useState(false);
  const [searchText, setSearchText] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [activePage, setActivePage] = useState("jobs");
  const [profileId, setProfileId] = useState(1);

  const loadJobs = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const data = await getJobMatches(profileId, {
        minScore: Number(minScore),
        maxMissingRequiredSkills:
          maxMissing === "" ? undefined : Number(maxMissing),
        fresherFriendlyOnly: fresherOnly,
      });

      setJobs(Array.isArray(data) ? data : []);
    } catch (err) {
      setError(
        err.message ||
          "Could not load job matches. Check your backend and profile ID.",
      );
    } finally {
      setLoading(false);
    }
  }, [profileId, minScore, maxMissing, fresherOnly]);

  useEffect(() => {
    loadJobs();
  }, [loadJobs]);

  const visibleJobs = jobs.filter((job) => {
    const query = searchText.trim().toLowerCase();

    return (
      !query ||
      [job.title, job.company, job.location].some((value) =>
        (value ?? "").toLowerCase().includes(query),
      )
    );
  });

  return (
    <main className="app-shell">
      <nav className="app-nav">
        <div className="app-nav-brand">
          <BriefcaseBusiness size={22} />
          <span>JobApply</span>
        </div>

        <div className="app-nav-links">
          <button
            type="button"
            className={activePage === "jobs" ? "nav-link active" : "nav-link"}
            onClick={() => setActivePage("jobs")}
          >
            <BriefcaseBusiness size={17} />
            Job Matches
          </button>

          <button
            type="button"
            className={activePage === "profile" ? "nav-link active" : "nav-link"}
            onClick={() => setActivePage("profile")}
          >
            <UserRound size={17} />
            My Profile
          </button>
        </div>
      </nav>

      {activePage === "profile" ? (
        <ProfilePage onProfileCreated={setProfileId} />
      ) : (
        <>
          <header className="topbar">
            <a className="brand" href="/">
              <span className="brand-icon">
                <BriefcaseBusiness size={21} />
              </span>
              <span>
                Job<span className="brand-accent">Apply</span>
              </span>
            </a>
            <span className="profile-label">
              <GraduationCap size={17} /> Candidate dashboard
            </span>
          </header>

          <section className="hero">
            <div className="hero-copy">
              <span className="eyebrow">YOUR CAREER STARTS HERE</span>
              <h1>
                Find jobs that <span>fit you.</span>
              </h1>
              <p>
                Discover opportunities matched to your skills, experience, and
                career preferences.
              </p>
            </div>
            <div className="hero-icon">
              <Search size={42} />
            </div>
          </section>

          <section className="dashboard-content">
            <div className="results-heading">
              <div>
                <h2>Job matches</h2>
                <p>
                  {loading
                    ? "Finding opportunities..."
                    : `${visibleJobs.length} matching ${visibleJobs.length === 1 ? "job" : "jobs"} found`}
                </p>
              </div>

              <button
                className="secondary-button"
                onClick={loadJobs}
                disabled={loading}
              >
                <RefreshCw size={16} className={loading ? "spin" : ""} />
                Refresh
              </button>
            </div>

            <section className="filter-panel">
              <div className="filter-title">
                <SlidersHorizontal size={18} />
                <h3>Refine results</h3>
              </div>

              <div className="filters">
                <label className="filter-field">
                  <span>Minimum match: {minScore}%</span>
                  <input
                    type="range"
                    min="0"
                    max="100"
                    step="5"
                    value={minScore}
                    onChange={(e) => setMinScore(Number(e.target.value))}
                  />
                </label>

                <label className="filter-field">
                  <span>Maximum missing required skills</span>
                  <select
                    value={maxMissing}
                    onChange={(e) => setMaxMissing(e.target.value)}
                  >
                    <option value="">Any number</option>
                    <option value="0">None missing</option>
                    <option value="1">At most 1</option>
                    <option value="2">At most 2</option>
                    <option value="3">At most 3</option>
                  </select>
                </label>

                <label className="filter-field search-field">
                  <span>Search jobs</span>
                  <div className="search-input-wrap">
                    <Search size={17} />
                    <input
                      type="search"
                      placeholder="Title, company, or location"
                      value={searchText}
                      onChange={(e) => setSearchText(e.target.value)}
                    />
                  </div>
                </label>

                <label className="checkbox-filter">
                  <input
                    type="checkbox"
                    checked={fresherOnly}
                    onChange={(e) => setFresherOnly(e.target.checked)}
                  />
                  <span>Fresher-friendly only</span>
                </label>
              </div>
            </section>

            {error && (
              <div className="error-state">
                <AlertCircle size={22} />
                <div>
                  <strong>Couldn't load job matches</strong>
                  <p>{error}</p>
                  <button className="secondary-button" onClick={loadJobs}>
                    Try again
                  </button>
                </div>
              </div>
            )}

            {loading && (
              <div className="loading-state">
                <div className="loader" />
                <p>Finding the best opportunities for you...</p>
              </div>
            )}

            {!loading && !error && visibleJobs.length === 0 && (
              <div className="empty-state">
                <Search size={32} />
                <h3>No jobs found</h3>
                <p>Try lowering your minimum score or changing your filters.</p>
              </div>
            )}

            {!loading && !error && visibleJobs.length > 0 && (
              <div className="job-list">
                {visibleJobs.map((job, index) => (
                  <JobCard key={job.jobId ?? index} job={job} />
                ))}
              </div>
            )}
          </section>
        </>
      )}

      <footer className="footer">
        JobApply · Find opportunities that fit your skills.
      </footer>
    </main>
  );
}
