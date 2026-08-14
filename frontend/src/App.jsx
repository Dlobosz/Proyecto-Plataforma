import { Routes, Route } from 'react-router-dom'
import Dashboard from './pages/Dashboard.jsx'
import ContentView from './pages/ContentView.jsx'
import ChallengeView from './pages/ChallengeView.jsx'

function App() {
  return (
    <Routes>
      <Route path="/" element={<Dashboard />} />
      <Route path="/units/:unitId" element={<ContentView />} />
      <Route path="/challenges/:challengeId" element={<ChallengeView />} />
    </Routes>
  )
}

export default App
