import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { ArrowLeft, Landmark, Save, ShieldCheck, KeyRound } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { api } from '../services/api'

export default function Profile() {
  const { user, refresh, clearSession } = useAuth()
  const navigate = useNavigate()
  const [profile, setProfile] = useState({ fullName: '', address: '', mobileNumber: '' })
  const [password, setPassword] = useState({ currentPassword: '', newPassword: '', confirmPassword: '' })
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')
  const [passwordMessage, setPasswordMessage] = useState('')
  const [passwordError, setPasswordError] = useState('')
  const [saving, setSaving] = useState(false)
  const [changing, setChanging] = useState(false)

  useEffect(() => {
    api('/profile').then(data => setProfile({
      fullName: data.fullName || '',
      address: data.address || '',
      mobileNumber: data.mobileNumber || ''
    })).catch(err => setError(err.message))
  }, [])

  function updateField(event) {
    setProfile(current => ({ ...current, [event.target.name]: event.target.value }))
  }

  async function saveProfile(event) {
    event.preventDefault()
    setSaving(true); setError(''); setMessage('')
    try {
      await api('/profile', { method: 'PUT', body: JSON.stringify(profile) })
      await refresh()
      setMessage('Your profile has been updated successfully.')
    } catch (err) {
      setError(err.message)
    } finally {
      setSaving(false)
    }
  }

  function updatePassword(event) {
    setPassword(current => ({ ...current, [event.target.name]: event.target.value }))
  }

  async function changePassword(event) {
    event.preventDefault()
    setPasswordError(''); setPasswordMessage('')
    if (password.newPassword !== password.confirmPassword) {
      setPasswordError('New password and confirmation do not match.')
      return
    }
    if (password.currentPassword === password.newPassword) {
      setPasswordError('Choose a password different from your current password.')
      return
    }
    setChanging(true)
    try {
      const result = await api('/profile/password', { method: 'PUT', body: JSON.stringify(password) })
      setPasswordMessage(result.message + ' Redirecting to sign in…')
      setTimeout(() => { clearSession(); navigate('/login', { replace: true }) }, 1500)
    } catch (err) {
      setPasswordError(err.message)
    } finally {
      setChanging(false)
    }
  }

  return <div className="app-shell"><aside className="sidebar"><Link to="/dashboard" className="brand-lockup"><div className="brand-mark"><Landmark size={22}/></div><span>Barangay<span className="brand-light">Connect</span></span></Link><div className="side-label">ACCOUNT</div><Link to="/dashboard" className="side-link"><ArrowLeft size={18}/> Back to dashboard</Link><div className="side-bottom"><div className="side-help"><ShieldCheck size={18}/><span><b>Private profile</b><small>Only you can edit your details</small></span></div></div></aside><main className="main-area"><header className="dash-top"><div><span className="crumb">Account / Profile</span><h2>Profile and password</h2></div><div className="top-user"><div className="avatar">{user?.fullName?.[0]?.toUpperCase()}</div><div><b>{user?.fullName}</b><small>{user?.role?.replaceAll('_',' ')}</small></div></div></header><section className="profile-intro"><p className="eyebrow green">ACCOUNT SETTINGS</p><h1>Manage your profile</h1><p>Keep your personal information accurate and your account secure.</p></section><section className="settings-panel"><div className="settings-heading"><div className="settings-icon"><Landmark size={19}/></div><div><h3>Personal information</h3><p>Update the details associated with your account.</p></div></div>{error&&<div className="alert">{error}</div>}{message&&<div className="success-msg">{message}</div>}<form className="form-grid settings-form" onSubmit={saveProfile}><label className="full">Full name<input name="fullName" value={profile.fullName} onChange={updateField} maxLength="120" required/></label><label className="full">Email address<input value={user?.email || ''} disabled/><small>Email address is used as your account identifier and cannot be changed here.</small></label><label>Mobile number<input name="mobileNumber" value={profile.mobileNumber} onChange={updateField} pattern="09[0-9]{9}" maxLength="11" required/></label><label>Barangay address<input name="address" value={profile.address} onChange={updateField} maxLength="220" required/></label><button className="primary-btn full" disabled={saving}>{saving?'Saving…':'Save profile changes'} <Save size={16}/></button></form></section><section className="settings-panel"><div className="settings-heading"><div className="settings-icon"><KeyRound size={19}/></div><div><h3>Change password</h3><p>Confirm your current password before choosing a new one.</p></div></div>{passwordError&&<div className="alert">{passwordError}</div>}{passwordMessage&&<div className="success-msg">{passwordMessage}</div>}<form className="form-grid settings-form" onSubmit={changePassword}><label className="full">Current password<input type="password" name="currentPassword" autoComplete="current-password" value={password.currentPassword} onChange={updatePassword} required/></label><label>New password<input type="password" name="newPassword" autoComplete="new-password" minLength="8" maxLength="72" value={password.newPassword} onChange={updatePassword} required/><small>Use 8 to 72 characters.</small></label><label>Confirm new password<input type="password" name="confirmPassword" autoComplete="new-password" minLength="8" maxLength="72" value={password.confirmPassword} onChange={updatePassword} required/></label><button className="primary-btn full" disabled={changing}>{changing?'Updating…':'Update password'} <KeyRound size={16}/></button></form></section><footer className="dash-footer">BarangayConnect · Your account, your information.</footer></main></div>
}
