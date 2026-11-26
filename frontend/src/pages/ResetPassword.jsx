import { useState } from 'react'
import { Link, useNavigate, useSearchParams } from 'react-router-dom'
import { ArrowLeft, CheckCircle2, ShieldCheck } from 'lucide-react'
import { api } from '../services/api'

export default function ResetPassword() {
  const [params] = useSearchParams()
  const navigate = useNavigate()
  const token = params.get('token') || ''
  const [newPassword, setNewPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [busy, setBusy] = useState(false)

  async function submit(event) {
    event.preventDefault()
    setError('')
    if (newPassword !== confirmPassword) {
      setError('Password confirmation does not match.')
      return
    }
    setBusy(true)
    try {
      const result = await api('/profile/reset-password', {
        method: 'POST',
        body: JSON.stringify({ token, newPassword, confirmPassword }),
      })
      setSuccess(result.message)
      setTimeout(() => navigate('/login'), 1800)
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
          <p className="eyebrow">SECURE YOUR ACCOUNT</p>
          <h1>
            A stronger password. <em>A safer account.</em>
          </h1>
          <p>Create a new password that you have not used for this account before.</p>
        </div>
        <div className="brand-foot">A digital service for better community support.</div>
      </section>
      <section className="auth-panel">
        <div className="auth-card">
          <div className="mobile-brand">
            Barangay<span>Connect</span>
          </div>
          <p className="eyebrow green">NEW PASSWORD</p>
          <h2>Choose a new password</h2>
          <p className="muted">
            Your reset link is valid for 15 minutes and can only be used once.
          </p>
          {!token ? (
            <div className="alert">This reset link is missing or invalid. Request a new one.</div>
          ) : (
            <form className="form-stack" onSubmit={submit}>
              <label>
                New password
                <input
                  type="password"
                  autoComplete="new-password"
                  minLength="8"
                  maxLength="72"
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  required
                />
                <small>Use 8 to 72 characters.</small>
              </label>
              <label>
                Confirm new password
                <input
                  type="password"
                  autoComplete="new-password"
                  minLength="8"
                  maxLength="72"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  required
                />
              </label>
              {error && <div className="alert">{error}</div>}
              {success && (
                <div className="success-msg">
                  <CheckCircle2 size={15} /> {success}
                </div>
              )}
              <button className="primary-btn" disabled={busy || !!success}>
                {busy ? 'Updating…' : 'Reset password'}
              </button>
            </form>
          )}
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
