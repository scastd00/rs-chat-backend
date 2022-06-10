import './App.css'
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import PublicRoute from './routes/PublicRoute';
import Login from './pages/Login';
import Register from './pages/Register';
import PrivacyPolicy from './pages/PrivacyPolicy';
import TermsAndConditions from './pages/TermsAndConditions';

function App() {
  return (
    <div className="App">
      <Router>
        <Routes>

          {/* If not logged in, go to log in */}
          <Route path='/' element={<Navigate to='/login'/>} />
          <Route path='/login' element={<PublicRoute component={Login} restricted />} />
          <Route path='/register' element={<PublicRoute component={Register} restricted />} />

          <Route path='/privacy' element={<PublicRoute component={PrivacyPolicy} />} />
          <Route path='/terms' element={<PublicRoute component={TermsAndConditions} />} />
        </Routes>
      </Router>
    </div>
  )
}

export default App
