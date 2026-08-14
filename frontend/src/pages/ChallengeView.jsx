import { useCallback, useEffect, useRef, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import Editor from '@monaco-editor/react'
import { getChallenge, submitChallenge } from '../api/client'

const MONACO_LANGUAGE = {
  OUTPUT: 'python',
  RESULTSET: 'sql',
}

const DIFICULTAD_LABEL = {
  FACIL: 'Facil',
  MEDIO: 'Medio',
  DIFICIL: 'Dificil',
}

function ChallengeView() {
  const { challengeId } = useParams()
  const [challenge, setChallenge] = useState(null)
  const [codigo, setCodigo] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const [pistasAbiertas, setPistasAbiertas] = useState(false)
  const [enviando, setEnviando] = useState(false)
  const [resultado, setResultado] = useState(null)
  const [errorEnvio, setErrorEnvio] = useState(null)

  // Divisor arrastrable entre el panel de contexto y el editor (sin libreria extra).
  const [leftWidth, setLeftWidth] = useState(38) // %
  const containerRef = useRef(null)
  const draggingRef = useRef(false)

  useEffect(() => {
    setLoading(true)
    setError(null)
    setResultado(null)
    getChallenge(challengeId)
      .then((data) => {
        setChallenge(data)
        setCodigo(data.codigoInicial ?? '')
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [challengeId])

  const onDividerMouseDown = useCallback(() => {
    draggingRef.current = true
  }, [])

  useEffect(() => {
    const onMouseMove = (e) => {
      if (!draggingRef.current || !containerRef.current) return
      const rect = containerRef.current.getBoundingClientRect()
      const pct = ((e.clientX - rect.left) / rect.width) * 100
      setLeftWidth(Math.min(70, Math.max(20, pct)))
    }
    const onMouseUp = () => {
      draggingRef.current = false
    }
    window.addEventListener('mousemove', onMouseMove)
    window.addEventListener('mouseup', onMouseUp)
    return () => {
      window.removeEventListener('mousemove', onMouseMove)
      window.removeEventListener('mouseup', onMouseUp)
    }
  }, [])

  const handleSubmit = async () => {
    setEnviando(true)
    setErrorEnvio(null)
    try {
      const data = await submitChallenge(challengeId, codigo)
      setResultado(data)
    } catch (err) {
      setErrorEnvio(err.response?.data?.message ?? err.message)
    } finally {
      setEnviando(false)
    }
  }

  if (loading) return <div className="container">Cargando...</div>
  if (error) return <div className="container">Error al cargar el desafio: {error}</div>
  if (!challenge) return null

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100vh' }}>
      <header style={{ padding: '0.75rem 1.5rem', borderBottom: '1px solid var(--border)' }}>
        <Link to={challenge.unitId ? `/units/${challenge.unitId}` : '/'}>← Volver a la unidad</Link>
      </header>

      <div ref={containerRef} style={{ flex: 1, display: 'flex', overflow: 'hidden' }}>
        <div style={{ width: `${leftWidth}%`, overflowY: 'auto', padding: '1.5rem' }}>
          <h1>{challenge.titulo}</h1>
          <p>
            <strong>Dificultad:</strong> {DIFICULTAD_LABEL[challenge.dificultad] ?? challenge.dificultad}
          </p>
          <p style={{ whiteSpace: 'pre-wrap' }}>{challenge.enunciado}</p>

          {challenge.pistas && (
            <div className="card" style={{ marginTop: '1rem' }}>
              <button className="btn-primary" onClick={() => setPistasAbiertas((v) => !v)}>
                {pistasAbiertas ? 'Ocultar pistas' : 'Ver pistas'}
              </button>
              {pistasAbiertas && (
                <p style={{ whiteSpace: 'pre-wrap', marginTop: '0.75rem' }}>{challenge.pistas}</p>
              )}
            </div>
          )}
        </div>

        <div
          onMouseDown={onDividerMouseDown}
          style={{ width: '6px', cursor: 'col-resize', background: 'var(--border)', flexShrink: 0 }}
        />

        <div style={{ flex: 1, display: 'flex', flexDirection: 'column', minWidth: 0 }}>
          <div style={{ flex: 1, minHeight: 0 }}>
            <Editor
              height="100%"
              language={MONACO_LANGUAGE[challenge.tipoValidacion] ?? 'plaintext'}
              theme="vs-dark"
              value={codigo}
              onChange={(value) => setCodigo(value ?? '')}
              options={{ minimap: { enabled: false }, fontSize: 14 }}
            />
          </div>

          <div style={{ borderTop: '1px solid var(--border)', padding: '1rem', overflowY: 'auto', maxHeight: '40%' }}>
            <button className="btn-primary" onClick={handleSubmit} disabled={enviando}>
              {enviando ? 'Ejecutando...' : 'Enviar'}
            </button>

            {errorEnvio && <p style={{ color: 'var(--fail)' }}>{errorEnvio}</p>}

            {resultado && (
              <div style={{ marginTop: '1rem' }}>
                <p>
                  Resultado:{' '}
                  <strong style={{ color: resultado.resultado === 'PASSED' ? 'var(--ok)' : 'var(--fail)' }}>
                    {resultado.resultado}
                  </strong>
                </p>
                {resultado.detalle.map((caso) => (
                  <div key={caso.validationCaseId} className="card" style={{ marginBottom: '0.5rem' }}>
                    <p style={{ margin: 0 }}>
                      {caso.passed ? '✅' : '❌'} Caso #{caso.validationCaseId}
                    </p>
                    {!caso.passed && (
                      <>
                        <p style={{ margin: '0.5rem 0 0' }}>
                          <strong>Esperado:</strong>
                        </p>
                        <pre style={{ margin: 0, whiteSpace: 'pre-wrap' }}>{caso.expectedOutput}</pre>
                        <p style={{ margin: '0.5rem 0 0' }}>
                          <strong>Obtenido:</strong>
                        </p>
                        <pre style={{ margin: 0, whiteSpace: 'pre-wrap' }}>{caso.actualOutput}</pre>
                        {caso.errorMessage && (
                          <p style={{ color: 'var(--fail)', margin: '0.5rem 0 0' }}>{caso.errorMessage}</p>
                        )}
                      </>
                    )}
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}

export default ChallengeView
