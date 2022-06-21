import React, { useEffect, useState } from 'react';
import { AppBar, Button, Grid, IconButton, Menu, MenuItem, Toolbar, Typography } from '@mui/material';
import { useNavigate } from 'react-router';
import { connect, useDispatch, useStore } from 'react-redux';
import { changeTheme, logOut } from '../actions';
import ShoppingCartTwoToneIcon from '@mui/icons-material/ShoppingCartTwoTone';
import HomeTwoToneIcon from '@mui/icons-material/HomeTwoTone';
import DarkModeTwoToneIcon from '@mui/icons-material/DarkModeTwoTone';
import Inventory2TwoToneIcon from '@mui/icons-material/Inventory2TwoTone';
import LoginTwoToneIcon from '@mui/icons-material/LoginTwoTone';
import LogoutTwoToneIcon from '@mui/icons-material/LogoutTwoTone';
import HowToRegTwoToneIcon from '@mui/icons-material/HowToRegTwoTone';
import AccountCircleTwoToneIcon from '@mui/icons-material/AccountCircleTwoTone';
import LightModeTwoToneIcon from '@mui/icons-material/LightModeTwoTone';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import SupervisorAccountTwoToneIcon from '@mui/icons-material/SupervisorAccountTwoTone';
import DesignServicesTwoToneIcon from '@mui/icons-material/DesignServicesTwoTone';
import BallotTwoToneIcon from '@mui/icons-material/BallotTwoTone';
import FavoriteTwoToneIcon from '@mui/icons-material/FavoriteTwoTone';
import InventoryTwoToneIcon from '@mui/icons-material/InventoryTwoTone';

function ToolBar(props) {
  const [loggedIn, setLoggedIn] = useState(false);
  const [anchorEl, setAnchorEl] = useState(null);
  const open = Boolean(anchorEl);
  const store = useStore();

  const [darkMode, setDarkMode] = useState(store.getState().theme.isDarkTheme);

  useEffect(() => {
    setDarkMode(store.getState().theme.isDarkTheme);
  }, [store.getState().theme]);

  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [username, setUsername] = useState('');

  useEffect(() => {
    if (!!props.data.user.username) {
      // If username is defined
      setUsername(props.data.user.username);
      setLoggedIn(true);
    } else {
      setUsername('');
      setLoggedIn(false);
    }
  }, [props.data.user]);

  return (
    <AppBar position='relative'>
      <Toolbar>
        <Grid container direction='row' justifyContent='space-between' alignItems='center'>
          <Grid item>
            <Grid container spacing={2}>
              <Grid item>
                <Button variant='text' color='secondary' onClick={() => navigate('/home')}>
                  <HomeTwoToneIcon sx={{ mr: 1 }} fontSize='medium' />
                  Home
                </Button>
              </Grid>
            </Grid>
          </Grid>

          <Grid item>
            <Button variant='text' color='secondary' onClick={handleClick}>
              <AccountCircleIcon fontSize='large' />
              {
                // If the user is logged in, show the username
                username.length !== 0 && (
                  <Typography variant='text' color='secondary' fontSize='medium' sx={{ pl: 1, textTransform: 'none' }}>
                    {username}
                  </Typography>
                )
              }
            </Button>

            <IconButton variant='text' color='secondary' onClick={() => dispatch(changeTheme())}>
              {darkMode ? <LightModeTwoToneIcon fontSize='large' /> : <DarkModeTwoToneIcon fontSize='large' />}
            </IconButton>

            <Menu anchorEl={anchorEl} open={open} onClose={handleClose}>
              {loggedIn ? (
                <div>
                  {store.getState().user.user.role === 'admin' ? (
                    <MenuItem
                      onClick={() => {
                        setAnchorEl(null);
                        navigate('/administration');
                      }}
                    >
                      <AccountCircleTwoToneIcon sx={{ mr: 1 }} />
                      Administration
                    </MenuItem>
                  ) : (
                    ''
                  )}
                  <MenuItem
                    onClick={() => {
                      setAnchorEl(null);
                      navigate('/profile');
                    }}
                  >
                    <SupervisorAccountTwoToneIcon sx={{ mr: 1 }} />
                    View Profile
                  </MenuItem>
                  <MenuItem
                    onClick={() => {
                      dispatch(logOut());
                      setAnchorEl(null);
                      navigate('/login');
                    }}
                  >
                    <LogoutTwoToneIcon sx={{ mr: 1 }} />
                    LogOut
                  </MenuItem>
                </div>
              ) : (
                <div>
                  <MenuItem
                    onClick={() => {
                      setAnchorEl(null);
                      navigate('/login');
                    }}
                  >
                    <LoginTwoToneIcon sx={{ mr: 1 }} />
                    LogIn
                  </MenuItem>
                  <MenuItem
                    onClick={() => {
                      setAnchorEl(null);
                      navigate('/register');
                    }}
                  >
                    <HowToRegTwoToneIcon sx={{ mr: 1 }} />
                    Register
                  </MenuItem>
                </div>
              )}
            </Menu>
          </Grid>
        </Grid>
      </Toolbar>
    </AppBar>
  );
}

const mapStateToProps = (state) => {
  return {
    data: state.user,
  };
};

export default connect(mapStateToProps)(ToolBar);
