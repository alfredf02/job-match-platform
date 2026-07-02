import Link from "next/link";

const featureCards = [
  {
    title: "Candidate Flow",
    description:
      "Register, sign in, maintain your profile, and review explainable job recommendations.",
    href: "/register",
    cta: "Create Account",
  },
  {
    title: "Employer Flow",
    description:
      "Set up an employer record, publish roles, and manage job postings from one place.",
    href: "/employer",
    cta: "Employer Setup",
  },
  {
    title: "Public Jobs",
    description:
      "Browse the current role catalogue with simple search filters powered by the job service.",
    href: "/jobs",
    cta: "Browse Jobs",
  },
];

export default function Home() {
  return (
    <div className="mx-auto max-w-5xl space-y-8">
      <section className="rounded-2xl border bg-white p-8 shadow-sm">
        <div className="max-w-3xl space-y-4">
          <p className="text-sm font-medium uppercase tracking-[0.2em] text-gray-500">
            Job Match Platform
          </p>
          <h1 className="text-4xl font-semibold tracking-tight text-black sm:text-5xl">
            Connect candidate profiles to relevant roles with a clean MVP workflow.
          </h1>
          <p className="text-base text-gray-600 sm:text-lg">
            This frontend integrates the completed user, job, and matching services with a
            simple candidate journey and a lightweight employer job management flow.
          </p>
          <div className="flex flex-wrap gap-3">
            <Link
              href="/register"
              className="inline-flex rounded-md bg-black px-4 py-2 text-sm text-white transition hover:bg-gray-800"
            >
              Get Started
            </Link>
            <Link
              href="/jobs"
              className="inline-flex rounded-md border border-gray-300 px-4 py-2 text-sm text-gray-700 transition hover:border-black hover:text-black"
            >
              Browse Jobs
            </Link>
          </div>
        </div>
      </section>

      <section className="grid gap-4 md:grid-cols-3">
        {featureCards.map((card) => (
          <div key={card.title} className="rounded-xl border bg-white p-6 shadow-sm">
            <div className="space-y-3">
              <h2 className="text-lg font-semibold text-black">{card.title}</h2>
              <p className="text-sm text-gray-600">{card.description}</p>
              <Link
                href={card.href}
                className="inline-flex rounded-md border border-gray-300 px-4 py-2 text-sm text-gray-700 transition hover:border-black hover:text-black"
              >
                {card.cta}
              </Link>
            </div>
          </div>
        ))}
      </section>
    </div>
  );
}
