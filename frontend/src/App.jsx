import { Routes, Route } from "react-router-dom";
import ProtectedRoute from "./components/ProtectedRoute";
import AppShell from "./components/AppShell";

import Login from "./pages/Login";
import Register from "./pages/Register";
import OAuthCallback from "./pages/OAuthCallback";
import Dashboard from "./pages/Dashboard";
import Events from "./pages/Events";
import EventDetail from "./pages/EventDetail";
import CompanionRequestNew from "./pages/CompanionRequestNew";
import Matches from "./pages/Matches";
import Groups from "./pages/Groups";
import GroupNew from "./pages/GroupNew";
import GroupDetail from "./pages/GroupDetail";
import GroupEdit from "./pages/GroupEdit";
import GroupChatRoom from "./pages/GroupChatRoom";
import ChatList from "./pages/ChatList";
import ChatRoom from "./pages/ChatRoom";
import Profile from "./pages/Profile";
import NotFound from "./pages/NotFound";

function Shell({ children }) {
  return (
    <ProtectedRoute>
      <AppShell>{children}</AppShell>
    </ProtectedRoute>
  );
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/oauth2/callback" element={<OAuthCallback />} />

      <Route path="/" element={<Shell><Dashboard /></Shell>} />
      <Route path="/events" element={<Shell><Events /></Shell>} />
      <Route path="/events/:id" element={<Shell><EventDetail /></Shell>} />
      <Route path="/companion-requests/new" element={<Shell><CompanionRequestNew /></Shell>} />
      <Route path="/matches" element={<Shell><Matches /></Shell>} />
      <Route path="/groups" element={<Shell><Groups /></Shell>} />
      <Route path="/groups/new" element={<Shell><GroupNew /></Shell>} />
      <Route path="/groups/:id" element={<Shell><GroupDetail /></Shell>} />
      <Route path="/groups/:id/edit" element={<Shell><GroupEdit /></Shell>} />
      <Route path="/groups/:id/chat" element={<Shell><GroupChatRoom /></Shell>} />
      <Route path="/chat" element={<Shell><ChatList /></Shell>} />
      <Route path="/chat/:id" element={<Shell><ChatRoom /></Shell>} />
      <Route path="/profile" element={<Shell><Profile /></Shell>} />

      <Route path="*" element={<NotFound />} />
    </Routes>
  );
}
