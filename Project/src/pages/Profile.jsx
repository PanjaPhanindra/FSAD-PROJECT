import React, { useState, useContext } from "react";
import { AuthContext } from "../context/AuthContext.jsx";
import { CartContext } from "../context/CartContext.jsx";
import { motion, AnimatePresence } from "framer-motion";
import AnimatedButton from "../components/AnimatedButton.jsx";
import Loader from "../components/Loader.jsx";

/**
 * Profile.jsx
 * - User settings: name, avatar, password, status
 * - Shows recent orders, review-giving panel
 * - Delete account, logout, etc.
 * - Animated, lively and extensible
 * - Handles all user types (buyer, seller, admin)
 **/

const AVATAR_OPTIONS = [
  "/assets/avatar-buyer.png",
  "/assets/avatar-seller.png",
  "/assets/avatar-admin.png",
  "/assets/avatar-guest.png"
];

export default function Profile() {
  const { user, updateProfile, deleteAccount, logout } = useContext(AuthContext);
  const { getOrdersForUser, leaveOrderReview } = useContext(CartContext);
  const [editMode, setEditMode] = useState(false);
  const [edit, setEdit] = useState({
    name: user?.name,
    avatarUrl: user?.avatarUrl,
    email: user?.email
  });
  const [pwd, setPwd] = useState({ old: "", new1: "", new2: "" });
  const [showPwd, setShowPwd] = useState(false);
  const [editError, setEditError] = useState("");
  const [toast, setToast] = useState("");
  const [loading, setLoading] = useState(false);

  // Orders for this user
  const orders = getOrdersForUser ? getOrdersForUser(user?.email) : [];

  // Handle save for profile edit
  async function saveProfile(e) {
    e.preventDefault();
    setEditError("");
    if (!edit.name || edit.name.length < 2) {
      setEditError("Please enter your full name.");
      return;
    }
    await updateProfile({ name: edit.name, avatarUrl: edit.avatarUrl });
    setEditMode(false);
    setToast("Profile updated!");
  }

  // Password change simulation
  async function changePwd(e) {
    e.preventDefault();
    setEditError("");
    setLoading(true);
    if (!pwd.old || !pwd.new1 || !pwd.new2) {
      setEditError("Fill all password fields.");
      setLoading(false);
      return;
    }
    if (pwd.new1 !== pwd.new2) {
      setEditError("New passwords do not match.");
      setLoading(false);
      return;
    }
    if (pwd.new1.length < 6) {
      setEditError("Password too short.");
      setLoading(false);
      return;
    }
    // Simulate successful change
    setTimeout(() => {
      setLoading(false);
      setEditError("");
      setPwd({ old: "", new1: "", new2: "" });
      setShowPwd(false);
      setToast("Password changed!");
    }, 1600);
  }

  // Remove account
  function confirmDelete() {
    if (window.confirm("Are you SURE you want to permanently delete your account? This cannot be undone!")) {
      deleteAccount();
    }
  }

  // Review handling
  function writeReview(orderId, productId) {
    const review = window.prompt("Write a short review (50 chars max):");
    if (review && review.length < 51) {
      leaveOrderReview(orderId, productId, { user: user.name, text: review, rating: 5 });
      setToast("Review submitted!");
    }
  }

  //---------------------- RENDER --------------------------

  return (
    <div className="min-h-screen bg-gradient-to-br from-[#e7edff] to-[#dafbe4] px-6 pt-10 pb-24">
      {/* Profile card */}
      <motion.section className="max-w-4xl mx-auto bg-white/90 rounded-3xl shadow-xl p-7 mb-10 flex flex-col md:flex-row gap-7 items-start relative"
        initial={{ opacity: 0, y: 23 }}
        animate={{ opacity: 1, y: 0 }}
        exit={{ opacity: 0 }}
      >
        <div className="flex flex-col items-center justify-start min-w-[188px] gap-3">
          <img src={edit.avatarUrl || "/assets/avatar-buyer.png"} className="avatar mb-1" alt="avatar"/>
          <span className={`font-bold text-green-900 text-lg`}>{edit.name}</span>
          <span className="text-xs font-semibold px-3 py-1 bg-green-50 rounded">{user.role}</span>
          <span className="text-xs bg-gray-100 text-gray-500 px-2 py-0.5 mt-1 rounded">{user.email}</span>
          <span className={`status-tag status-${user.status}`}>Status: {user.status}</span>
          {/* Change avatar (edit mode) */}
          {editMode && (
            <div className="flex flex-wrap gap-2 pt-3 justify-center items-center">
              {AVATAR_OPTIONS.map(a => (
                <button key={a} onClick={()=>setEdit(e=>({...e, avatarUrl: a}))} className={"p-1 rounded-full "+(edit.avatarUrl===a?"ring-2 ring-green-400":"")}>
                  <img src={a} className="h-10 w-10 object-cover rounded-full" alt="choose avatar"/>
                </button>
              ))}
            </div>
          )}
        </div>
        {/* Edit details */}
        <form onSubmit={saveProfile} className="flex flex-1 flex-col gap-2">
          <label>
            Name
            <input type="text" value={edit.name} onChange={e=>setEdit(e=>({...e, name: e.target.value}))} disabled={!editMode}/>
          </label>
          <label>
            Email
            <input type="email" value={edit.email} disabled className="bg-gray-100"/>
          </label>
          {editMode ? (
            <div className="flex gap-4 mt-4">
              <AnimatedButton type="submit" color="primary" label="Save" />
              <AnimatedButton type="button" color="secondary" label="Cancel" onClick={()=>setEditMode(false)} />
            </div>
          ) : (
            <div className="flex gap-4 mt-4">
              <AnimatedButton type="button" color="primary" label="Edit Profile" onClick={()=>setEditMode(true)} />
              <AnimatedButton type="button" color="danger" label="Delete Account" onClick={confirmDelete} />
              <AnimatedButton type="button" color="secondary" label="Logout" onClick={logout} />
            </div>
          )}
        </form>
        {editError && <div className="form-error absolute right-3 top-3">{editError}</div>}
      </motion.section>

      {/* Password change panel */}
      <motion.section className="max-w-4xl mx-auto bg-white/90 rounded-2xl shadow-lg p-6 mb-8 flex-col flex gap-2"
        initial={{ opacity: 0, y: 19 }}
        animate={{ opacity: 1, y: 0 }}
      >
        <span className="mb-1 font-bold text-green-800">Change Password</span>
        <form onSubmit={changePwd} className="flex flex-wrap gap-3 items-end">
          <label className="flex-1">Old Password
            <input type={showPwd ? "text" : "password"} value={pwd.old} onChange={e=>setPwd(p=>({...p,old: e.target.value}))}/>
          </label>
          <label className="flex-1">New Password
            <input type={showPwd ? "text" : "password"} value={pwd.new1} onChange={e=>setPwd(p=>({...p,new1: e.target.value}))}/>
          </label>
          <label className="flex-1">Confirm New
            <input type={showPwd ? "text" : "password"} value={pwd.new2} onChange={e=>setPwd(p=>({...p,new2: e.target.value}))}/>
          </label>
          <button type="button" className="ml-2 px-3 py-1 text-xs bg-green-100 rounded" onClick={()=>setShowPwd(v=>!v)}>{showPwd ? "Hide" : "Show"} Passwords</button>
          <AnimatedButton label="Change" color="primary" loading={loading}/>
        </form>
      </motion.section>
      {/* ORDER HISTORY & REVIEWS */}
      <motion.section className="max-w-5xl mx-auto bg-white/80 rounded-xl shadow p-8 mb-7"
        initial={{ opacity: 0, y: 16 }}
        animate={{ opacity: 1, y: 0 }}
      >
        <h2 className="text-xl font-bold text-green-900 mb-5">My Orders & Reviews</h2>
        {orders.length === 0 ? (
          <div className="text-center text-gray-500 py-7">You have not yet placed any orders!</div>
        ) : (
          <table className="table-admin">
            <thead>
              <tr>
                <th>Order</th>
                <th>Date</th>
                <th>Product</th>
                <th>Status</th>
                <th>Review</th>
              </tr>
            </thead>
            <tbody>
              {orders.map(order =>
                order.items.map(item => (
                  <tr key={order.id + '-' + item.id}>
                    <td>{order.id}</td>
                    <td>{new Date(order.createdAt).toLocaleDateString()}</td>
                    <td>{item.name}</td>
                    <td><span className={`status-tag status-${order.status === "Cancelled" ? "pending" : "active"}`}>{order.status}</span></td>
                    <td>
                      {item.review ? (
                        <span className="bg-yellow-50 rounded-lg px-2 py-1 text-xs text-yellow-700">Given</span>
                      ) : (
                        <AnimatedButton
                          label="Write Review"
                          color="secondary"
                          size="sm"
                          onClick={() => writeReview(order.id, item.id)}
                        />
                      )}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        )}
      </motion.section>
      {/* Toast notification */}
      <AnimatePresence>
        {toast && (
          <motion.div 
            className="toast"
            initial={{ opacity: 0, y: 18 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: 18 }}
          >
            {toast}
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}
