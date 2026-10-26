import { useState } from "react";
import { createProfile, getProfile, updateProfile } from "./api";
import { UserRound, Save, Search, LoaderCircle } from "lucide-react";

const emptyProfile = {
  fullName: "",
  email: "",
  phone: "",
  location: "",
  linkedinUrl: "",
  githubUrl: "",
  summary: "",
};

export default function ProfilePage({ onProfileCreated }) {
  const [profile, setProfile] = useState(emptyProfile);
  const [lookupId, setLookupId] = useState("");
  const [profileId, setProfileId] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  function handleChange(event) {
    const { name, value } = event.target;

    setProfile((current) => ({
      ...current,
      [name]: value,
    }));
  }

  async function handleSave(event) {
    event.preventDefault();
    setLoading(true);
    setError("");
    setMessage("");

    try {
      const saved =
        profileId !== null
          ? await updateProfile(profileId, profile)
          : await createProfile(profile);

      setProfile(saved);
      setProfileId(saved.id);
      setLookupId(String(saved.id));

      setMessage(
        profileId !== null
          ? "Profile updated successfully."
          : "Profile created successfully.",
      );

      if (onProfileCreated) {
        onProfileCreated(saved.id);
      }
    } catch (err) {
      setError(err.message || "Could not save profile.");
    } finally {
      setLoading(false);
    }
  }

  async function handleLoad(event) {
    event.preventDefault();
    setLoading(true);
    setError("");
    setMessage("");

    try {
      const existing = await getProfile(lookupId);
      setProfile({
        fullName: existing.fullName ?? "",
        email: existing.email ?? "",
        phone: existing.phone ?? "",
        location: existing.location ?? "",
        linkedinUrl: existing.linkedinUrl ?? "",
        githubUrl: existing.githubUrl ?? "",
        summary: existing.summary ?? "",
      });
      setProfileId(existing.id);
      setMessage(`Loaded profile ${existing.id}.`);

      if (onProfileCreated) {
        onProfileCreated(existing.id);
      }
    } catch (err) {
      setError(err.message || "Could not load profile.");
    } finally {
      setLoading(false);
    }
  }

  const fields = [
    { name: "fullName", label: "Full name", required: true },
    { name: "email", label: "Email address", type: "email", required: true },
    { name: "phone", label: "Phone number", type: "tel" },
    {
      name: "location",
      label: "Location",
      placeholder: "e.g. Guwahati, Assam",
    },
    { name: "linkedinUrl", label: "LinkedIn URL", type: "url" },
    { name: "githubUrl", label: "GitHub URL", type: "url" },
  ];

  return (
    <main className="profile-page">
      <header className="profile-heading">
        <div className="profile-heading-icon">
          <UserRound size={25} />
        </div>
        <div>
          <h1>Your professional profile</h1>
          <p>Manage the details used by your job-matching platform.</p>
        </div>
      </header>

      <form className="profile-lookup" onSubmit={handleLoad}>
        <label htmlFor="lookupId">Already have a profile?</label>
        <div className="profile-lookup-row">
          <input
            id="lookupId"
            type="number"
            min="1"
            value={lookupId}
            onChange={(event) => setLookupId(event.target.value)}
            placeholder="Enter profile ID"
            required
          />
          <button type="submit" disabled={loading || !lookupId}>
            <Search size={16} />
            Load profile
          </button>
        </div>
      </form>

      <form className="profile-form" onSubmit={handleSave}>
        <h2>Personal and professional details</h2>
        <p className="profile-form-description">
          Fields marked with * are required.
        </p>

        <div className="profile-fields">
          {fields.map((field) => (
            <div className="profile-field" key={field.name}>
              <label htmlFor={field.name}>
                {field.label}
                {field.required ? " *" : ""}
              </label>
              <input
                id={field.name}
                name={field.name}
                type={field.type || "text"}
                value={profile[field.name]}
                onChange={handleChange}
                placeholder={field.placeholder || ""}
                required={field.required || false}
              />
            </div>
          ))}
        </div>

        <div className="profile-field profile-summary-field">
          <label htmlFor="summary">Professional summary</label>
          <textarea
            id="summary"
            name="summary"
            value={profile.summary}
            onChange={handleChange}
            placeholder="Briefly describe your background, strengths, and career goals."
            rows={5}
          />
        </div>

        {error && <p className="profile-error">{error}</p>}
        {message && <p className="profile-success">{message}</p>}

        {profileId !== null && (
          <p className="profile-id">Current profile ID: {profileId}</p>
        )}

        <button className="profile-submit" type="submit" disabled={loading}>
          {loading ? (
            <LoaderCircle size={17} className="profile-spinner" />
          ) : (
            <Save size={17} />
          )}
          {loading
            ? "Please wait..."
            : profileId !== null
              ? "Save changes"
              : "Create profile"}
        </button>
      </form>
    </main>
  );
}
