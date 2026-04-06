import React, { useState, useRef } from "react";
import { motion, AnimatePresence } from "framer-motion";
import AnimatedButton from "../components/AnimatedButton.jsx";

/**
 * Contact.jsx
 * - Contact info, animated map/graphic, form for inquiries/feedback/support
 * - Live validation, submission animation, professional and friendly tone
 */

const CONTACTS = [
  { label: "Support", value: "support@farmconnect.com", icon: "🛡️" },
  { label: "Sales", value: "sales@farmconnect.com", icon: "💼" },
  { label: "Phone", value: "1800-555-FARM", icon: "📞" }
];

export default function Contact() {
  const [form, setForm] = useState({ name: "", email: "", message: "" });
  const [sent, setSent] = useState(false);
  const [err, setErr] = useState("");
  const nameInput = useRef();

  // Simple validation
  function validate() {
    if (!form.name || form.name.length < 2) return "Your name, please!";
    if (!form.email || !form.email.includes("@")) return "Valid email required.";
    if (!form.message || form.message.length < 8) return "Tell us a little more...";
    return "";
  }

  // Simulate send
  function submit(e) {
    e.preventDefault();
    const v = validate();
    setErr("");
    if (v) {
      setErr(v);
      return;
    }
    setSent(true);
    setTimeout(() => {
      setSent(false);
      setForm({ name: "", email: "", message: "" });
      setErr("");
    }, 1700);
  }

  return (
    <div className="min-h-screen flex flex-col items-center bg-gradient-to-br from-[#ecfffb] to-[#fffbe5] pt-14 pb-24 px-5">
      <motion.section
        className="max-w-4xl mx-auto bg-white/98 rounded-3xl shadow-2xl p-10 mb-12"
        initial={{ opacity: 0, y: 28 }}
        animate={{ opacity: 1, y: 0 }}
      >
        <h2 className="text-3xl md:text-4xl font-black text-green-900 mb-4 text-center">Contact Us</h2>
        <div className="flex flex-col md:flex-row gap-10 items-start">
          <div className="flex-1">
            <div className="mb-7 text-green-900 font-bold">Reach out for help, support, or to join our network:</div>
            <div className="mb-6 flex flex-col gap-4">
              {CONTACTS.map(c => (
                <div key={c.label} className="flex items-center gap-3 bg-green-50 px-5 py-2.5 rounded-xl font-medium shadow">
                  <span className="text-2xl">{c.icon}</span>
                  <span className="font-bold">{c.label}:</span>
                  <span className="text-green-800">{c.value}</span>
                </div>
              ))}
            </div>
            <motion.div
  initial={{ opacity: 0, y: 36, scale: 0.93 }}
  animate={{ opacity: 1, y: 0, scale: 1 }}
  transition={{ duration: 0.6 }}
  className="w-full md:w-96 rounded-2xl overflow-hidden shadow-lg"
>
  <iframe 
    src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d122103.71422743557!2d81.72395666393108!3d16.98728214373291!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x3a37a3f2440c9fff%3A0x86b24503e305ca21!2sRajamahendravaram%2C%20Andhra%20Pradesh!5e0!3m2!1sen!2sin!4v1762352964527!5m2!1sen!2sin" 
    width="100%" 
    height="300" 
    style={{ border: 0 }} 
    allowFullScreen="" 
    loading="lazy" 
    referrerPolicy="no-referrer-when-downgrade"
    className="w-full"
  />
</motion.div>

          </div>
          {/* Inquiry Form */}
          <form
            onSubmit={submit}
            className="flex-1 flex flex-col gap-4 bg-green-50/80 p-7 rounded-xl shadow-lg"
            aria-label="Contact form"
          >
            <label>Name
              <input
                ref={nameInput}
                value={form.name}
                onChange={e => setForm(f => ({ ...f, name: e.target.value }))}
                className="w-full"
                disabled={sent}
                autoFocus
              />
            </label>
            <label>Email
              <input
                value={form.email}
                onChange={e => setForm(f => ({ ...f, email: e.target.value }))}
                type="email"
                className="w-full"
                disabled={sent}
              />
            </label>
            <label>Your Message
              <textarea
                value={form.message}
                onChange={e => setForm(f => ({ ...f, message: e.target.value }))}
                className="w-full"
                minLength={8}
                rows={3}
                disabled={sent}
              />
            </label>
            {err && <div className="form-error">{err}</div>}
            <AnimatedButton
              type="submit"
              color="secondary"
              loading={sent}
              disabled={sent}
              label={sent ? "Sending..." : "Send"}
            />
            <AnimatePresence>
              {sent && (
                <motion.div
                  className="toast"
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  exit={{ opacity: 0, y: 20 }}
                >Thank you! We'll get in touch soon.</motion.div>
              )}
            </AnimatePresence>
          </form>
        </div>
      </motion.section>
    </div>
  );
}
