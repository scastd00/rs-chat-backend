import React from 'react';
import { Navigate } from 'react-router-dom';
import { isLoggedIn } from './utils';

/**
 * If the route is restricted, redirect to home page.
 * If not, render the component.
 *
 * **If the user is logged in and the route is restricted, redirect to home page.**
 *
 * @param Component - Component to render
 * @param restricted - Boolean to check if route is restricted
 * @returns {JSX.Element} - Component to render
 */
export default function PublicRoute({ component: Component, restricted = false }) {
  return isLoggedIn() && restricted ? <Navigate to='/home' /> : <Component />;
}
