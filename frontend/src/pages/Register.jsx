import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { ArrowRight, Landmark } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
export default function Register() {
  const [form, setForm] = useState({
    fullName: '',
    address: '',
    mobileNumber: '',
    email: '',
    password: '',
  })
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)
  const { register } = useAuth()
  const nav = useNavigate()
  function change(e) {
    setForm({ ...form, [e.target.name]: e.target.value })
  }
  async function submit(e) {
    e.preventDefault()
    setError('')
    setBusy(true)
    try {
      await register(form)
      nav('/dashboard')
    } catch (err) {
      setError(err.message)
    } finally {
      setBusy(false)
    }
  }
  return (
    <main className="register-layout">
      <header className="topbar">
        <Link to="/login" className="brand-lockup dark">
          <div className="brand-mark">
            <Landmark size={22} />
          </div>
          <span>
            Barangay<span className="brand-light">Connect</span>
          </span>
        </Link>
        <span className="topbar-note">Resident registration</span>
      </header>
      <section className="register-card">
        <p className="eyebrow green">GET STARTED</p>
        <h1>Create your resident account</h1>
        <p className="muted">Register to access barangay services and track your concerns.</p>
        <form onSubmit={submit} className="form-grid">
          <label className="full">
            Full name
            <input
              name="fullName"
              value={form.fullName}
              onChange={change}
              placeholder="Juan dela Cruz"
              maxLength="120"
              required
            />
          </label>
          <label className="full">
            Complete address
            <input
              name="address"
              value={form.address}
              onChange={change}
              placeholder="House no., street, purok, barangay"
              maxLength="220"
              required
            />
          </label>
          <label>
            Mobile number
            <input
              name="mobileNumber"
              value={form.mobileNumber}
              onChange={change}
              placeholder="09XXXXXXXXX"
              pattern="09[0-9]{9}"
              required
            />
          </label>
          <label>
            Email address
            <input
              name="email"
              type="email"
              value={form.email}
              onChange={change}
              placeholder="you@example.com"
              required
            />
          </label>
          <label className="full">
            Password
            <input
              name="password"
              type="password"
              value={form.password}
              onChange={change}
              minLength="8"
              maxLength="72"
              placeholder="At least 8 characters"
              required
            />
            <small>Use at least 8 characters. Keep your password private.</small>
          </label>
          <div className="consent full">
            <input type="checkbox" required />
            <span>
              I understand that my information will be used to provide BarangayConnect services and
              handled according to the Data Privacy Act.
            </span>
          </div>
          {error && <div className="alert full">{error}</div>}
          <button className="primary-btn full" disabled={busy}>
            {busy ? 'Creating account…' : 'Create account'} <ArrowRight size={17} />
          </button>
        </form>
        <p className="form-foot">
          Already registered? <Link to="/login">Sign in</Link>
        </p>
      </section>
    </main>
  )
}
