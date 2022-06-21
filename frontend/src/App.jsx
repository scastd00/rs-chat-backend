import './App.css';
import { BrowserRouter as Router, Navigate, Route, Routes } from 'react-router-dom';
import { connect } from 'react-redux';
import PublicRoute from './routes/PublicRoute';
import Login from './pages/Login';
import Register from './pages/Register';
import PrivacyPolicy from './pages/PrivacyPolicy';
import TermsAndConditions from './pages/TermsAndConditions';
import { useEffect, useState } from 'react';
import { ThemeProvider } from '@mui/material/styles';
import { darkTheme as dark, lightTheme as light } from './themes';
import ToolBar from './layouts/ToolBar';

function App(props) {
  // Theme detector -> https://medium.com/hypersphere-codes/detecting-system-theme-in-javascript-css-react-f6b961916d48
  const [darkTheme, setDarkTheme] = useState(props.theme.isDarkTheme);

  useEffect(() => {
    // If darkTheme parameter is changed, every change
    // made to code is reflected in UI when saved.
    setDarkTheme(props.theme.isDarkTheme);
  }, [props.theme.isDarkTheme]);

  return (
    <ThemeProvider theme={darkTheme ? dark : light}>
      <div className='App'>
        <Router>
          <ToolBar />

          <Routes>

            {/* If not logged in, go to log in */}
            <Route path='/' element={<Navigate to='/login' />} />
            <Route path='/login' element={<PublicRoute component={Login} restricted />} />
            <Route path='/register' element={<PublicRoute component={Register} restricted />} />

            <Route path='/privacy' element={<PublicRoute component={PrivacyPolicy} />} />
            <Route path='/terms' element={<PublicRoute component={TermsAndConditions} />} />
          </Routes>
        </Router>
      </div>
    </ThemeProvider>
  );
}

/*
 For more info see the following video
 https://www.youtube.com/watch?v=gFZiQnM3Is4
 */

const mapStateToProps = (state) => {
  return {
    theme: state.theme,
  };
};

export default connect(mapStateToProps)(App);
