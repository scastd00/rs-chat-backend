import Api from './Api';

export default {
  login(credentials) {
    return Api.post('/hello', credentials);
  },

  get() {
    return Api.get("/hello");
  }
};
