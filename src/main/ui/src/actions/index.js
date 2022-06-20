import { createAction } from '@reduxjs/toolkit';
import { CHANGE_THEME, LOG_OUT, SET_USER, SET_USER_TOKEN } from './constants';

export const setUser = createAction(SET_USER, (user) => {
  return { payload: user };
});

export const setUserToken = createAction(SET_USER_TOKEN, (token) => {
  return { payload: token };
});

export const logOut = createAction(LOG_OUT);
export const changeTheme = createAction(CHANGE_THEME);
