import type { Metadata } from "next";
import Link from "next/link";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "Job Match Platform",
  description: "Job Match Platform frontend",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body className={`${geistSans.variable} ${geistMono.variable} antialiased`}>
        <div className="min-h-screen bg-white text-black">
          <nav className="border-b">
            <div className="mx-auto flex max-w-5xl items-center justify-between px-4 py-3">
              <Link href="/" className="text-lg font-semibold">
                Job Match Platform
              </Link>
              <div className="flex items-center gap-4 text-sm">
                <Link href="/login">Login</Link>
                <Link href="/register">Register</Link>
                <Link href="/dashboard">Dashboard</Link>
              </div>
            </div>
          </nav>
          <main className="mx-auto max-w-5xl px-4 py-8">{children}</main>
        </div>
      </body>
    </html>
  );
}
