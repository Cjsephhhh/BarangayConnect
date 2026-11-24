import { useState } from 'react'
import { Link } from 'react-router-dom'
import { ArrowLeft, Mail, ShieldCheck } from 'lucide-react'
import { api } from '../services/api'

export default function ForgotPassword() {
  const [email, setEmail] = useState('')
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  async function submit(event) {
    event.preventDefault()
    setBusy(true)
    setError('')
    setMessage('')
    try {
      const result = await api('/profile/forgot-password', {
        method: 'POST',
        body: JSON.stringify({ email }),
      })
      setMessage(result.message)
    } catch (err) {
      setError(err.message)
    } finally {
      setBusy(false)
    }
  }

  return (
    <main className="auth-layout">
      <section className="brand-panel">
        <div className="brand-lockup">
          <div className="brand-mark">
            <ShieldCheck size={25} />
          </div>
          <span>
            Barangay<span className="brand-light">Connect</span>
          </span>
        </div>
        <div className="brand-copy">
          <p className="eyebrow">ACCOUNT RECOVERY</p>
          <h1>
            Get back to your <em>community.</em>
          </h1>
          <p>
            We'll send a secure, time-limited password reset link to your registered email address.
          </p>
        </div>
        <div className="brand-foot">A digital service for better community support.</div>
      </section>
      <section className="auth-panel">
        <div className="auth-card">
          <div className="mobile-brand">
            Barangay<span>Connect</span>
          </div>
          <p className="eyebrow green">FORGOT PASSWORD</p>
          <h2>Reset your password</h2>
          <p className="muted">
            Enter the email address connected to your account. If it exists, we'll send a reset
            link.
          </p>
          <form className="form-stack" onSubmit={submit}>
            <label>
              Registered email address
              <input
                type="email"
                autoComplete="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="you@example.com"
                required
              />
            </label>
            {error && <div className="alert">{error}</div>}
            {message && <div className="success-msg">{message}</div>}
            <button className="primary-btn" disabled={busy}>
              {busy ? 'Sending…' : 'Send reset link'} <Mail size={17} />
            </button>
          </form>
          <p className="form-foot">
            <Link to="/login">
              <ArrowLeft size={13} /> Back to sign in
            </Link>
          </p>
        </div>
      </section>
    </main>
  )
}
