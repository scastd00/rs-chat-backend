import { createTheme } from '@mui/material/styles';

export default createTheme({
  mixins: {
    toolbar: {
      minHeight: 60,
      height: 60,
      maxHeight: 60,
    },
  },
  palette: {
    mode: 'dark',
    primary: {
      main: '#e7a09b',
    },
    secondary: {
      main: '#648888',
    },
    error: {
      main: '#f44336',
    },
    warning: {
      main: '#ffa726',
    },
    info: {
      main: '#29b6f6',
    },
    success: {
      main: '#66bb6a',
    },
    text: {
      primary: '#fff',
      secondary: 'rgba(255, 255, 255, 0.7)',
      disabled: 'rgba(255, 255, 255, 0.5)',
    },
    background: {
      paper: '#121420',
      default: '#121420',
    },
    modifiable: {
      main: 'rgba(102,187,106,0.3)',
    },
    notModifiable: {
      main: 'rgba(244,67,54,0.3)',
    },
    footer: {
      main: '#66a4a4',
    },
  },
});
