import { useEffect } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { setToken } from '../utils/auth'

function OAuth2RedirectHandler() {
  const navigate = useNavigate()
  const location = useLocation()

  useEffect(() => {
    const params = new URLSearchParams(location.search)
    const token = params.get('token')

    if (token) {
      setToken(token)
      navigate('/profile')
    } else {
      navigate('/login', { state: { error: 'OAuth2 login failed' } })
    }
  }, [location, navigate])

  return (
    <div className="page">
      <div className="card">
        <h1>Processing login...</h1>
        <p>Please wait while we finalize your authentication.</p>
      </div>
    </div>
  )
}

export default OAuth2RedirectHandler
