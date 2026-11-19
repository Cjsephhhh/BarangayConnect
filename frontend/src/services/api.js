const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1'
export async function api(path, options = {}) {
 const token = localStorage.getItem('bc_token')
 const headers = { 'Content-Type': 'application/json', ...(token ? { Authorization: 'Bearer ' + token } : {}), ...options.headers }
 const response = await fetch(API_URL + path, { ...options, headers })
 const body = response.status === 204 ? null : await response.json().catch(() => ({}))
 if (!response.ok) throw new Error(body.message || body.detail || body.error || ('Request failed (' + response.status + ')'))
 return body
}