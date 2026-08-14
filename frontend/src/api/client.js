import axios from 'axios'

const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:18081/api'

const api = axios.create({ baseURL })

export const getLanguages = () =>
  api.get('/languages').then((res) => res.data)

export const getUnitsByLanguage = (languageId) =>
  api.get(`/languages/${languageId}/units`).then((res) => res.data)

export const getUnit = (unitId) =>
  api.get(`/units/${unitId}`).then((res) => res.data)

export const getChallengesByUnit = (unitId) =>
  api.get(`/units/${unitId}/challenges`).then((res) => res.data)

export const getChallenge = (challengeId) =>
  api.get(`/challenges/${challengeId}`).then((res) => res.data)

export const submitChallenge = (challengeId, codigo) =>
  api.post(`/challenges/${challengeId}/submit`, { codigo }).then((res) => res.data)

export default api
