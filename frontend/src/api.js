const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

export async function getJobMatches(
  profileId,
  { minScore = 0, maxMissingRequiredSkills, fresherFriendlyOnly = false } = {},
) {
  const params = new URLSearchParams();

  params.set("minScore", String(minScore));

  if (
    maxMissingRequiredSkills !== undefined &&
    maxMissingRequiredSkills !== null
  ) {
    params.set("maxMissingRequiredSkills", String(maxMissingRequiredSkills));
  }

  if (fresherFriendlyOnly) {
    params.set("fresherFriendlyOnly", "true");
  }

  const response = await fetch(
    `${API_BASE_URL}/profiles/${profileId}/job-matches?${params}`,
  );

  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || `Request failed: ${response.status}`);
  }

  return response.json();
}

export async function createProfile(profileData) {
  const response = await fetch(`${API_BASE_URL}/profiles`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(profileData),
  });

  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || `Request failed: ${response.status}`);
  }

  return response.json();
}

export async function getProfile(profileId) {
  const response = await fetch(`${API_BASE_URL}/profiles/${profileId}`);

  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || `Request failed: ${response.status}`);
  }

  return response.json();
}


export async function updateProfile(profileId, profileData) {
  const response = await fetch(
    `${API_BASE_URL}/profiles/${profileId}`,
    {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(profileData),
    },
  );

  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || `Request failed: ${response.status}`);
  }

  return response.json();
}
