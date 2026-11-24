import { createContext, useContext, useEffect, useState } from 'react'
import { api } from '../services/api'
const AuthContext = createContext(null)
export function AuthProvider({ children }) {
 const [user,setUser]=useState(null);const [loading,setLoading]=useState(true)
 useEffect(()=>{if(!localStorage.getItem('bc_token')){setLoading(false);return}api('/auth/me').then(setUser).catch(()=>{localStorage.removeItem('bc_token');setUser(null)}).finally(()=>setLoading(false))},[])
 async function accept(result){localStorage.setItem('bc_token',result.token);setUser(result.user);return result.user}
 async function login(identifier,password){return accept(await api('/auth/login',{method:'POST',body:JSON.stringify({identifier,password})}))}
 async function register(data){return accept(await api('/auth/register',{method:'POST',body:JSON.stringify(data)}))}
 async function logout(){try{await api('/auth/logout',{method:'POST'})}finally{localStorage.removeItem('bc_token');setUser(null)}}
 function clearSession(){localStorage.removeItem('bc_token');setUser(null)}\n return <AuthContext.Provider value={{user,loading,login,register,logout,clearSession,refresh:async()=>setUser(await api('/auth/me'))}}>{children}</AuthContext.Provider>
}
export const useAuth=()=>useContext(AuthContext)