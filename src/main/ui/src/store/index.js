import storage from 'redux-persist/lib/storage';
import { configureStore } from '@reduxjs/toolkit';
import { FLUSH, PAUSE, PERSIST, persistReducer, PURGE, REGISTER, REHYDRATE } from 'redux-persist';
import appReducer from '../reducers';

const persistConfig = {
  key: 'state',
  storage: storage,
};

const rootReducer = (state, action) => {
  if (action.type === 'logOut') {
    storage.removeItem('persist:state');

    return appReducer(undefined, action); // Set default state of the store on logout
  }

  return appReducer(state, action);
};

const persistentReducer = persistReducer(persistConfig, rootReducer);

export default configureStore({
  reducer: persistentReducer,
  middleware: (getDefaultMiddleware) => {
    return getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: [FLUSH, REHYDRATE, PAUSE, PERSIST, PURGE, REGISTER],
      },
    });
  },
  devTools: process.env.NODE_ENV !== 'production',
});

// More info at https://dev.to/dawnind/persist-redux-state-with-redux-persist-3k0d
