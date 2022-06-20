import ArrowRightAltIcon from '@mui/icons-material/ArrowRightAlt';
import InfoIcon from '@mui/icons-material/Info';
import VpnKeyIcon from '@mui/icons-material/VpnKey';
import { VisibilityOff, Visibility } from '@mui/icons-material';
import {
  Avatar,
  Box,
  Button,
  Container,
  CssBaseline,
  Grid,
  Link,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  TextField,
  Typography,
  InputAdornment,
  IconButton,
  FormControlLabel,
  Checkbox,
  Snackbar,
  Alert,
} from '@mui/material';
import React, { useState } from 'react';
import { useDispatch } from 'react-redux';
import { useNavigate } from 'react-router';
import { setUser, setUserToken } from '../actions';
import AuthService from '../services/AuthService';

function Register() {
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const [registerError, setRegisterError] = useState('');
  const [password, setPassword] = useState('');
  const [usernameError, setUsernameError] = useState('');
  const [passwordError, setPasswordError] = useState('');
  const [confirmationError, setConfirmationError] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [termsAccepted, setTermsAccepted] = useState(false);
  const [snackbarError, setSnackbarError] = useState(false);

  const whatYouCanDo = ['Add products to favorites', 'Buy exclusive products', 'Receive special offers'];

  const handleSubmit = (event) => {
    event.preventDefault();

    if (!termsAccepted) {
      setSnackbarError(true);

      const timeout = setTimeout(() => {
        setSnackbarError(false);
        clearTimeout(timeout);
      }, 2500);

      return;
    }

    const data = new FormData(event.currentTarget);

    AuthService.register({
      username: data.get('username'),
      email: data.get('email'),
      password: data.get('password'),
      confirmPassword: data.get('confirmPassword'),
      termsAccepted,
    })
               .then((res) => {
                 dispatch(setUser(res.data.user));
                 dispatch(setUserToken(res.data.token));

                 navigate('/home');
               })
               .catch((e) => {
                 setRegisterError(e.response.data.error);
               });
  };

  const checkUsername = (event) => {
    const len = event.target.value.length;

    if (len < 3 || len > 25) {
      setUsernameError('Username must be between 3 and 25 characters');
    } else {
      setUsernameError('');
    }
  };

  const checkPassword = (event) => {
    const pass = event.target.value;
    const len = pass.length;
    setPassword(pass);

    if (len < 8 || len > 32) {
      setPasswordError('Password must be between 8 and 32 characters');
    } else {
      setPasswordError('');
    }
  };

  const checkConfirmPassword = (event) => {
    const confirmation = event.target.value;

    if (password !== confirmation) {
      setConfirmationError('Passwords do not match');
    } else {
      setConfirmationError('');
    }
  };

  const togglePasswordMask = () => {
    setShowPassword(!showPassword);
  };

  const inputPropsForPasswordMask = {
    endAdornment: (
      <InputAdornment position='end'>
        <IconButton onMouseDown={togglePasswordMask} onMouseUp={togglePasswordMask}>
          {showPassword ? <VisibilityOff /> : <Visibility />}
        </IconButton>
      </InputAdornment>
    ),
  };

  return (
    <Container component='main'>
      <CssBaseline />

      <Grid container spacing={16} direction='row' justifyContent='center'>
        <Grid item xs={7.5}>
          <Box sx={{
            marginTop: 5,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
          }}>
            <Avatar sx={{ m: 1, bgcolor: 'secondary.main' }}>
              <VpnKeyIcon />
            </Avatar>

            <Typography component='h1' variant='h5'>
              Register
            </Typography>

            <Box component='form' noValidate onSubmit={handleSubmit} sx={{ mt: 4 }}>
              <Grid container spacing={4}>
                <Grid item xs={12}>
                  <TextField
                    required
                    fullWidth
                    id='email'
                    label='Email Address'
                    autoFocus
                    name='email'
                    autoComplete='email'
                    onChange={() => setRegisterError('')}
                    variant='standard'
                    color='secondary'
                  />
                </Grid>

                <Grid item xs={12}>
                  <TextField
                    required
                    autoComplete='given-name'
                    name='username'
                    fullWidth
                    id='username'
                    label='Username'
                    onChange={() => setRegisterError('')}
                    helperText={usernameError}
                    error={usernameError.length !== 0}
                    onInput={checkUsername}
                    variant='standard'
                    color='secondary'
                  />
                </Grid>

                <Grid item xs={12}>
                  <TextField
                    required
                    fullWidth
                    name='password'
                    label='Password'
                    type={showPassword ? 'text' : 'password'}
                    id='password'
                    autoComplete='new-password'
                    onChange={() => setRegisterError('')}
                    helperText={passwordError}
                    error={passwordError.length !== 0}
                    onInput={checkPassword}
                    variant='standard'
                    color='secondary'
                    InputProps={inputPropsForPasswordMask}
                  />
                </Grid>

                <Grid item xs={12}>
                  <TextField
                    required
                    fullWidth
                    name='confirmPassword'
                    label='Confirm Password'
                    type={showPassword ? 'text' : 'password'}
                    id='confirmPassword'
                    onChange={() => setRegisterError('')}
                    helperText={confirmationError}
                    error={confirmationError.length !== 0}
                    onInput={checkConfirmPassword}
                    variant='standard'
                    color='secondary'
                    InputProps={inputPropsForPasswordMask}
                  />
                </Grid>

                {/* Show registerError when it is received from the server */}
                {registerError.length > 0 && <ErrorAlert content={registerError} />}
              </Grid>

              <Button type='submit' fullWidth variant='contained' sx={{ mt: 3, mb: 2 }}>
                Register
              </Button>

              <FormControlLabel
                onChange={(event) => {
                  setTermsAccepted(event.target.checked);
                }}
                checked={termsAccepted}
                control={<Checkbox value='terms' color='primary' />}
                label={
                  <Typography fontSize={14}>
                    Registering means you that you have read and agree to the{' '}
                    <Link underline='hover' style={{ cursor: 'pointer' }} onClick={() => navigate('/terms')}>
                      Terms of Service
                    </Link>{' '}
                    and{' '}
                    <Link underline='hover' style={{ cursor: 'pointer' }} onClick={() => navigate('/privacy')}>
                      Privacy Policy
                    </Link>
                    .
                  </Typography>
                }
              />

              <Typography textAlign='right'>
                <Link component='button' type='button' variant='body2' onClick={() => navigate('/login')} sx={{}}>
                  Already have an account? Log in
                </Link>
              </Typography>
            </Box>
          </Box>
        </Grid>
      </Grid>

      <Snackbar open={snackbarError} anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}>
        <Alert severity='error' variant='filled'>
          You must accept the terms and conditions to register.
        </Alert>
      </Snackbar>
    </Container>
  );
}

export default Register;
