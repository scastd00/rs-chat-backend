import { createReducer } from '@reduxjs/toolkit';
import { logOut, setUser, setUserToken } from '../actions';

export const userReducer = createReducer({ user: {}, token: '' }, (builder) => {
  builder
    .addCase(setUser, (state, action) => {
      state.user = action.payload; // Set the new user
    })
    .addCase(setUserToken, (state, action) => {
      state.token = action.payload; // Set the new user token
    })
    .addCase(logOut, (state, _action) => {
      state.user = {};
      state.token = '';
      // Clear the user in the app
    });
});
