import React from 'react';
import { Button } from '@mui/material';
import AuthService from '../services/AuthService';

function Login() {
  function handleLogin() {
    AuthService
      .get(/*{ name: 'Samuel', password: 'Samuel' }*/)
      .then((res) => {
        console.log(res);
      });
    AuthService
      .login({ text: 'Hola', role: 'ROLE_USER' })
      .then((res) => {
        console.log(res);
      });
  }

  return (
    <h1>
      Login
      <p>
        <Button onClick={ handleLogin }>Hola</Button>
      </p>
    </h1>
  );
}

export default Login;
