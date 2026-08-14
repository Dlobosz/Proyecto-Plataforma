import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { getUnit, getChallengesByUnit } from '../api/client'

const DIFICULTAD_LABEL = {
  FACIL: 'Facil',
  MEDIO: 'Medio',
  DIFICIL: 'Dificil',
}

function ContentView() {
  const { unitId } = useParams()
  const [unit, setUnit] = useState(null)
  const [challenges, setChallenges] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    setLoading(true)
    setError(null)
    Promise.all([getUnit(unitId), getChallengesByUnit(unitId)])
      .then(([unitData, challengesData]) => {
        setUnit(unitData)
        setChallenges(challengesData)
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [unitId])

  if (loading) return <div className="container">Cargando...</div>
  if (error) return <div className="container">Error al cargar la unidad: {error}</div>
  if (!unit) return null

  return (
    <div className="container">
      <p>
        <Link to="/">← Volver al panel</Link>
      </p>
      <h1>{unit.titulo}</h1>
      <article style={{ whiteSpace: 'pre-wrap', lineHeight: 1.6 }}>{unit.contenidoTeorico}</article>

      <h2>Desafios</h2>
      {challenges.length === 0 && <p>Esta unidad todavia no tiene desafios.</p>}
      <ul style={{ listStyle: 'none', padding: 0, display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
        {challenges.map((challenge) => (
          <li key={challenge.id} className="card">
            <Link to={`/challenges/${challenge.id}`}>
              {challenge.titulo} — {DIFICULTAD_LABEL[challenge.dificultad] ?? challenge.dificultad}
            </Link>
          </li>
        ))}
      </ul>
    </div>
  )
}

export default ContentView
