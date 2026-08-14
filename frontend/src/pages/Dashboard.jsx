import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getLanguages, getUnitsByLanguage } from '../api/client'

const ESTADO_ICONS = {
  BLOQUEADA: '🔒',
  DESBLOQUEADA: '🔄',
  COMPLETADA: '✅',
}

function Dashboard() {
  const [languages, setLanguages] = useState([])
  const [unitsByLanguage, setUnitsByLanguage] = useState({})
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    getLanguages()
      .then(async (langs) => {
        setLanguages(langs)
        const entries = await Promise.all(
          langs.map((lang) => getUnitsByLanguage(lang.id).then((units) => [lang.id, units])),
        )
        setUnitsByLanguage(Object.fromEntries(entries))
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <div className="container">Cargando...</div>
  if (error) return <div className="container">Error al cargar el panel: {error}</div>

  return (
    <div className="container">
      <h1>Panel de Progreso</h1>

      {languages.length === 0 && <p>Todavia no hay lenguajes cargados.</p>}

      {languages.map((lang) => (
        <section key={lang.id} style={{ marginBottom: '2rem' }}>
          <h2>{lang.nombre}</h2>
          <ol style={{ listStyle: 'none', padding: 0, display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
            {(unitsByLanguage[lang.id] ?? []).map((unit) => (
              <li key={unit.id} className="card">
                <Link to={`/units/${unit.id}`}>
                  {ESTADO_ICONS[unit.estado] ?? ''} {unit.orden}. {unit.titulo}
                </Link>
              </li>
            ))}
          </ol>
        </section>
      ))}
    </div>
  )
}

export default Dashboard
