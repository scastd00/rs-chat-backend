import { createReducer } from '@reduxjs/toolkit';
import { changeTheme } from '../actions';

export const themeReducer = createReducer({ isDarkTheme: true }, (builder) => {
  builder.addCase(changeTheme, (state, _action) => {
    state.isDarkTheme = !state.isDarkTheme;
  });
});
