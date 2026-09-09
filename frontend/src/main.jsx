import React,{useEffect,useState} from 'react'
import {createRoot} from 'react-dom/client'
import {BrowserRouter,useNavigate,useLocation} from 'react-router-dom'
import './style.css'

const API='http://localhost:8080/api'
const getToken=()=>localStorage.getItem('token')
async function api(path,options={}){
  const headers={'Content-Type':'application/json',...(options.headers||{})}
  if(getToken()) headers.Authorization=`Bearer ${getToken()}`
  const r=await fetch(API+path,{...options,headers})
  const text=await r.text()
  let data={}; try{data=text?JSON.parse(text):{}}catch{data={message:text}}
  if(!r.ok) throw new Error(data.message||'Request failed')
  return data
}

function Header({user,setUser}){
  const nav=useNavigate()
  return <header><div className="brand" onClick={()=>nav('/')}>🎒 Campus Lost & Found</div>
    <nav><button className="link" onClick={()=>nav('/')}>Browse</button>
    {user?<><button className="link" onClick={()=>nav('/report')}>Report Item</button><button className="link" onClick={()=>nav('/my')}>My Reports</button><button className="link" onClick={()=>{localStorage.clear();setUser(null);nav('/')}}>Logout</button></>
    :<><button className="link" onClick={()=>nav('/login')}>Login</button><button className="primary small" onClick={()=>nav('/register')}>Register</button></>}</nav>
  </header>
}

function Card({item,onClick}){
 return <div className="card" onClick={onClick}>
   {item.imageUrl?<img src={item.imageUrl} className="thumb"/>:<div className="thumb placeholder">📦</div>}
   <div className="cardbody"><div className="row"><span className={'badge '+item.type.toLowerCase()}>{item.type}</span><span className="muted">{item.date}</span></div>
   <h3>{item.title}</h3><p>{item.description||'No description provided.'}</p><div className="muted">📍 {item.location} · {item.category}</div></div>
 </div>
}

function Home(){
 const [items,setItems]=useState([]),[q,setQ]=useState(''),[type,setType]=useState('')
 const [loading,setLoading]=useState(true),nav=useNavigate()
 const load=()=>{setLoading(true);api(`/items?${q?`search=${encodeURIComponent(q)}&`:''}${type?`type=${type}`:''}`).then(setItems).finally(()=>setLoading(false))}
 useEffect(load,[type])
 return <main><section className="hero"><div><span className="eyebrow">YOUR CAMPUS, ONE PLACE</span><h1>Lost something?<br/><span>Let's find it.</span></h1><p>Report lost or found belongings and help your campus community reconnect them with their owners.</p><button className="primary" onClick={()=>nav(getToken()?'/report':'/login')}>Report an Item →</button></div><div className="heroicon">🔎</div></section>
 <section className="toolbar"><input value={q} onChange={e=>setQ(e.target.value)} onKeyDown={e=>e.key==='Enter'&&load()} placeholder="Search items, places, keywords..."/><select value={type} onChange={e=>setType(e.target.value)}><option value="">All reports</option><option value="LOST">Lost</option><option value="FOUND">Found</option></select><button className="secondary" onClick={load}>Search</button></section>
 <h2>Recent reports</h2>{loading?<div className="empty">Loading...</div>:items.length?<div className="grid">{items.map(i=><Card key={i.id} item={i} onClick={()=>nav('/item/'+i.id)}/>)}</div>:<div className="empty">No reports found.</div>}</main>
}

function Login({setUser}){const [f,setF]=useState({email:'',password:''}),[err,setErr]=useState(''),nav=useNavigate()
 const submit=async e=>{e.preventDefault();try{const d=await api('/auth/login',{method:'POST',body:JSON.stringify(f)});localStorage.setItem('token',d.token);localStorage.setItem('user',JSON.stringify(d));setUser(d);nav('/')}catch(e){setErr(e.message)}}
 return <Auth title="Welcome back" subtitle="Login to manage your reports."><form onSubmit={submit}><Input label="Email" type="email" value={f.email} onChange={v=>setF({...f,email:v})}/><Input label="Password" type="password" value={f.password} onChange={v=>setF({...f,password:v})}/>{err&&<div className="error">{err}</div>}<button className="primary full">Login</button></form></Auth>
}
function Register({setUser}){const [f,setF]=useState({name:'',email:'',password:''}),[err,setErr]=useState(''),nav=useNavigate()
 const submit=async e=>{e.preventDefault();try{const d=await api('/auth/register',{method:'POST',body:JSON.stringify(f)});localStorage.setItem('token',d.token);localStorage.setItem('user',JSON.stringify(d));setUser(d);nav('/')}catch(e){setErr(e.message)}}
 return <Auth title="Create your account" subtitle="Join your campus lost & found community."><form onSubmit={submit}><Input label="Name" value={f.name} onChange={v=>setF({...f,name:v})}/><Input label="Email" type="email" value={f.email} onChange={v=>setF({...f,email:v})}/><Input label="Password" type="password" value={f.password} onChange={v=>setF({...f,password:v})}/>{err&&<div className="error">{err}</div>}<button className="primary full">Create Account</button></form></Auth>
}
function Auth({title,subtitle,children}){return <main className="auth"><div className="authbox"><div className="logo">🎒</div><h1>{title}</h1><p>{subtitle}</p>{children}</div></main>}
function Input({label,type='text',value,onChange,placeholder}){return <label>{label}<input type={type} value={value} placeholder={placeholder} onChange={e=>onChange(e.target.value)} required/></label>}

function Report(){
 const [f,setF]=useState({title:'',description:'',category:'Electronics',location:'',date:new Date().toISOString().slice(0,10),imageUrl:'',type:'LOST'}),[err,setErr]=useState(''),nav=useNavigate()
 const set=(k,v)=>setF({...f,[k]:v})
 const submit=async e=>{e.preventDefault();try{await api('/items',{method:'POST',body:JSON.stringify(f)});nav('/my')}catch(e){setErr(e.message)}}
 return <main className="formpage"><div className="formbox"><h1>Report an item</h1><p className="muted">Give enough details so another student can recognize it.</p><form onSubmit={submit}><div className="toggle"><button type="button" className={f.type==='LOST'?'active':''} onClick={()=>set('type','LOST')}>I LOST IT</button><button type="button" className={f.type==='FOUND'?'active':''} onClick={()=>set('type','FOUND')}>I FOUND IT</button></div>
 <div className="twocol"><Input label="Item name" value={f.title} onChange={v=>set('title',v)} placeholder="e.g. Black Samsung Watch"/><label>Category<select value={f.category} onChange={e=>set('category',e.target.value)}><option>Electronics</option><option>Documents</option><option>Accessories</option><option>Books</option><option>Clothing</option><option>Keys</option><option>Other</option></select></label></div>
 <label>Description<textarea value={f.description} onChange={e=>set('description',e.target.value)} placeholder="Brand, color, identifying marks..."/></label>
 <div className="twocol"><Input label="Location" value={f.location} onChange={v=>set('location',v)} placeholder="e.g. Central Library"/><Input label="Date" type="date" value={f.date} onChange={v=>set('date',v)}/></div>
 <Input label="Image URL (optional)" value={f.imageUrl} onChange={v=>set('imageUrl',v)} placeholder="https://..."/>
 {err&&<div className="error">{err}</div>}<button className="primary full">Publish Report</button></form></div></main>
}

function ItemPage(){
 const id=location.pathname.split('/').pop(),[item,setItem]=useState(null),[matches,setMatches]=useState([]),nav=useNavigate()
 useEffect(()=>{api('/items/'+id).then(setItem);api('/items/'+id+'/matches').then(setMatches).catch(()=>{})},[id])
 if(!item)return <main><div className="empty">Loading...</div></main>
 return <main><button className="back" onClick={()=>nav(-1)}>← Back</button><div className="detail"><div>{item.imageUrl?<img src={item.imageUrl} className="detailimg"/>:<div className="detailimg placeholder">📦</div>}</div><div><span className={'badge '+item.type.toLowerCase()}>{item.type}</span><h1>{item.title}</h1><p>{item.description}</p><div className="facts"><div>📍 <b>Location</b><br/>{item.location}</div><div>📅 <b>Date</b><br/>{item.date}</div><div>🏷️ <b>Category</b><br/>{item.category}</div><div>👤 <b>Reported by</b><br/>{item.reporterName}</div></div>{item.type==='FOUND'&&<a className="primary inline" href={'mailto:'+item.reporterEmail+'?subject=Campus Lost & Found enquiry'}>Contact Reporter</a>}</div></div>
 {matches.length>0&&<><h2>Possible matches</h2><div className="grid">{matches.map(i=><Card key={i.id} item={i} onClick={()=>nav('/item/'+i.id)}/>)}</div></>}</main>
}

function MyReports(){
 const [items,setItems]=useState([]),nav=useNavigate()
 const load=()=>api('/items').then(all=>setItems(all.filter(i=>{const u=JSON.parse(localStorage.getItem('user')||'{}');return i.reporterEmail===u.email})))
 useEffect(load,[])
 const recover=async id=>{await api('/items/'+id+'/recover',{method:'PATCH'});load()}
 return <main><div className="pagehead"><div><span className="eyebrow">YOUR ACTIVITY</span><h1>My Reports</h1></div><button className="primary" onClick={()=>nav('/report')}>+ New Report</button></div>{items.length?<div className="grid">{items.map(i=><div key={i.id}><Card item={i} onClick={()=>nav('/item/'+i.id)}/>{i.status==='ACTIVE'&&<button className="recover" onClick={()=>recover(i.id)}>✓ Mark as recovered</button>}</div>)}</div>:<div className="empty">You haven't posted any reports yet.</div>}</main>
}

function App(){
 const [user,setUser]=useState(()=>JSON.parse(localStorage.getItem('user')||'null'))
 const {pathname:p}=useLocation()
 let content=p==='/login'?<Login setUser={setUser}/>:p==='/register'?<Register setUser={setUser}/>:p==='/report'?(user?<Report/>:<Login setUser={setUser}/>):p==='/my'?(user?<MyReports/>:<Login setUser={setUser}/>):p.startsWith('/item/')?<ItemPage/>:<Home/>
 return <><Header user={user} setUser={setUser}/>{content}<footer>Campus Lost & Found · Spring Boot + React · College Mini Project</footer></>
}
createRoot(document.getElementById('root')).render(<BrowserRouter><App/></BrowserRouter>)
