import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../api/axios'
import { removeToken } from '../utils/auth'

function ProfilePage() {
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const loadProfile = async () => {
      try {
        const response = await api.get('/api/profile')
        setEmail(response.data.email)
      } catch (requestError) {
        if (requestError.response?.status === 401) {
          navigate('/login', { replace: true })
          return
        }

        setError(requestError.response?.data?.message || 'Could not load profile')
      } finally {
        setLoading(false)
      }
    }

    loadProfile()
  }, [navigate])

  const handleLogout = () => {
    removeToken()
    navigate('/login')
  }

  return (
    <div className="page">
      <div className="card">
        <h1>Profile</h1>
        <p>This page is protected and requires a valid JWT token.</p>

        {loading ? <p className="message">Loading profile...</p> : null}
        {error ? <p className="message error">{error}</p> : null}

        {!loading && !error ? (
          <div className="profile-details">
            <strong>Email:</strong> {email}
          </div>
        ) : null}

        <div className="actions">
          <button className="button button-secondary" type="button" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </div>
    </div>
  )
}

export default ProfilePage
