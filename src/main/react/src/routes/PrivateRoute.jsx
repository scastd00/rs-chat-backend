import React from 'react';
import { isLoggedIn } from './utils';
import { Navigate } from 'react-router';

/**
 * If the user is logged in, render the component. Otherwise, redirect to the login page.
 *
 * @param Component The component to render if the user is logged in.
 * @returns {JSX.Element} - The resulting component of checking if the user is logged in.
 */
export default function PrivateRoute({ component: Component }) {
  return isLoggedIn() ? <Component /> : <Navigate to='/login' />;
}
