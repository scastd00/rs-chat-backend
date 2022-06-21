import Api from './Api';

export default {
  login(credentials) {
    console.log(credentials);
    return Api.post('/login', credentials);
  },
};
