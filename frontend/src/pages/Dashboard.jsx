import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { api } from '../services/api'
import {
  Landmark,
  LayoutDashboard,
  Users,
  FileText,
  CalendarDays,
  LogOut,
  ShieldCheck,
  ClipboardList,
  Activity,
  Plus,
  Bell,
} from 'lucide-react'
const roles = {
  RESIDENT: [
    'Resident',
    'Your community, connected.',
    'Access barangay services and keep track of your concerns.',
    ['My cases', 'Guided assistance', 'My profile'],
  ],
  BARANGAY_SECRETARY: [
    'Barangay Secretary',
    'Good day. Here is your workspace.',
    'Manage resident records, incoming complaints, and barangay schedules.',
    ['Case intake', 'Hearing schedule', 'Case records'],
  ],
  PUNONG_BARANGAY: [
    'Punong Barangay',
    'Lead with clarity and confidence.',
    'Oversee dispute resolution and barangay case activities.',
    ['Case oversight', 'Mediation', 'Reports'],
  ],
  LUPON_MEMBER: [
    'Lupon Member',
    'Your service makes a difference.',
    'Access cases assigned to you and support community conciliation.',
    ['Assigned cases', 'Hearing activities', 'My profile'],
  ],
  SYSTEM_ADMINISTRATOR: [
    'System Administrator',
    'System overview',
    'Manage user access and maintain the BarangayConnect platform.',
    ['User management', 'Official accounts', 'Audit trail'],
  ],
}
const icons = [<FileText size={18} />, <CalendarDays size={18} />, <ClipboardList size={18} />]
export default function Dashboard() {
  const { user, logout } = useAuth()
  const [users, setUsers] = useState([])
  const [logs, setLogs] = useState([])
  const [showCreate, setShowCreate] = useState(false)
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')
  const info = roles[user?.role] || roles.RESIDENT
  async function loadAdmin() {
    try {
      setUsers(await api('/admin/users'))
      setLogs(await api('/admin/audit-logs'))
    } catch (e) {
      setError(e.message)
    }
  }
  useEffect(() => {
    if (user?.role === 'SYSTEM_ADMINISTRATOR') loadAdmin()
  }, [user])
  async function createOfficial(e) {
    e.preventDefault()
    const f = new FormData(e.currentTarget)
    try {
      await api('/admin/officials', {
        method: 'POST',
        body: JSON.stringify({
          fullName: f.get('fullName'),
          email: f.get('email'),
          mobileNumber: f.get('mobileNumber'),
          address: f.get('address'),
          temporaryPassword: f.get('password'),
          role: f.get('role'),
        }),
      })
      setMessage('Official account created successfully.')
      setShowCreate(false)
      loadAdmin()
    } catch (err) {
      setError(err.message)
    }
  }
  async function toggle(id, active) {
    try {
      await api('/admin/users/' + id + '/status', {
        method: 'PATCH',
        body: JSON.stringify({ active }),
      })
      loadAdmin()
    } catch (e) {
      setError(e.message)
    }
  }
  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand-lockup">
          <div className="brand-mark">
            <Landmark size={22} />
          </div>
          <span>
            Barangay<span className="brand-light">Connect</span>
          </span>
        </div>
        <div className="side-label">WORKSPACE</div>
        <Link to="/profile" className="side-link">
          <ShieldCheck size={18} /> My profile & password
        </Link>
        <a className="side-link active">
          <LayoutDashboard size={18} /> Dashboard
        </a>
        {info[3].map((x, i) => (
          <a className="side-link" key={x}>
            {icons[i]}
            {x}
          </a>
        ))}
        <div className="side-bottom">
          <div className="side-help">
            <ShieldCheck size={18} />
            <span>
              <b>Secure workspace</b>
              <small>Your access is protected</small>
            </span>
          </div>
          <button className="logout-btn" onClick={logout}>
            <LogOut size={17} /> Sign out
          </button>
        </div>
      </aside>
      <main className="main-area">
        <header className="dash-top">
          <div>
            <span className="crumb">Workspace / Dashboard</span>
            <h2>{info[0]} dashboard</h2>
          </div>
          <div className="top-user">
            <button className="icon-btn" aria-label="Notifications">
              <Bell size={19} />
            </button>
            <div className="avatar">{user?.fullName?.[0]?.toUpperCase()}</div>
            <div>
              <b>{user?.fullName}</b>
              <small>{info[0]}</small>
            </div>
          </div>
        </header>
        <section className="welcome-card">
          <div>
            <p className="eyebrow">BARANGAYCONNECT WORKSPACE</p>
            <h1>{info[1]}</h1>
            <p>{info[2]}</p>
          </div>
          <div className="welcome-art">
            <Landmark size={76} strokeWidth={1} />
          </div>
        </section>
        {message && <div className="success-msg">{message}</div>}
        {error && <div className="alert">{error}</div>}
        <div className="section-heading">
          <div>
            <h3>Your workspace</h3>
            <p>Tools and information available to your account.</p>
          </div>
          {user?.role === 'SYSTEM_ADMINISTRATOR' && (
            <button className="small-primary" onClick={() => setShowCreate(!showCreate)}>
              <Plus size={16} /> Add official
            </button>
          )}
        </div>
        <div className="feature-grid">
          {info[3].map((x, i) => (
            <article className="feature-card" key={x}>
              <div className="feature-icon">{icons[i]}</div>
              <h4>{x}</h4>
              <p>
                {
                  [
                    'Access the tools and information assigned to your role.',
                    'Review and manage the information relevant to your work.',
                    'Keep your BarangayConnect workspace up to date.',
                  ][i]
                }
              </p>
              <span className="text-action">
                Available in your workspace <span>→</span>
              </span>
            </article>
          ))}
        </div>
        {user?.role === 'SYSTEM_ADMINISTRATOR' && (
          <>
            <div className="section-heading">
              <div>
                <h3>Account overview</h3>
                <p>Registered accounts in BarangayConnect.</p>
              </div>
            </div>
            <div className="stats-grid">
              <div className="stat-card">
                <Users />
                <span>Total accounts</span>
                <strong>{users.length}</strong>
              </div>
              <div className="stat-card">
                <ShieldCheck />
                <span>Active accounts</span>
                <strong>{users.filter((x) => x.active).length}</strong>
              </div>
              <div className="stat-card">
                <Activity />
                <span>Audit entries</span>
                <strong>{logs.length}</strong>
              </div>
            </div>
            {showCreate && (
              <form className="official-form" onSubmit={createOfficial}>
                <h3>Create barangay official account</h3>
                <div className="form-grid">
                  <label>
                    Full name
                    <input name="fullName" required />
                  </label>
                  <label>
                    Email
                    <input name="email" type="email" required />
                  </label>
                  <label>
                    Mobile number
                    <input name="mobileNumber" pattern="09[0-9]{9}" required />
                  </label>
                  <label>
                    Address
                    <input name="address" required />
                  </label>
                  <label>
                    Temporary password
                    <input name="password" type="password" minLength="8" required />
                  </label>
                  <label>
                    Role
                    <select name="role">
                      <option value="BARANGAY_SECRETARY">Barangay Secretary</option>
                      <option value="PUNONG_BARANGAY">Punong Barangay</option>
                      <option value="LUPON_MEMBER">Lupon Member</option>
                      <option value="SYSTEM_ADMINISTRATOR">System Administrator</option>
                    </select>
                  </label>
                </div>
                <button className="primary-btn">Create account</button>
              </form>
            )}
            <section className="data-panel">
              <div className="panel-heading">
                <div>
                  <h3>User accounts</h3>
                  <p>Manage registered residents and barangay officials.</p>
                </div>
                <span className="count-pill">{users.length} accounts</span>
              </div>
              <div className="table-wrap">
                <table>
                  <thead>
                    <tr>
                      <th>Name</th>
                      <th>Email</th>
                      <th>Role</th>
                      <th>Status</th>
                      <th>Access</th>
                    </tr>
                  </thead>
                  <tbody>
                    {users.map((u) => (
                      <tr key={u.id}>
                        <td>
                          <b>{u.fullName}</b>
                        </td>
                        <td>{u.email}</td>
                        <td>{u.role.replaceAll('_', ' ')}</td>
                        <td>
                          <span className="status-pill">{u.active ? 'Active' : 'Inactive'}</span>
                        </td>
                        <td>
                          <button className="table-action" onClick={() => toggle(u.id, !u.active)}>
                            Set {u.active ? 'inactive' : 'active'}
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </section>
            <section className="data-panel">
              <div className="panel-heading">
                <div>
                  <h3>Recent audit activity</h3>
                  <p>Account-related activity recorded by the system.</p>
                </div>
              </div>
              <div className="table-wrap">
                <table>
                  <thead>
                    <tr>
                      <th>Action</th>
                      <th>Actor</th>
                      <th>Entity</th>
                      <th>Date</th>
                    </tr>
                  </thead>
                  <tbody>
                    {logs.slice(0, 8).map((l) => (
                      <tr key={l.id}>
                        <td>{l.action}</td>
                        <td>{l.actorEmail || 'System'}</td>
                        <td>{l.entityType || '—'}</td>
                        <td>{new Date(l.createdAt).toLocaleString()}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </section>
          </>
        )}
        <footer className="dash-footer">
          BarangayConnect · A digital service for better community support.
        </footer>
      </main>
    </div>
  )
}
