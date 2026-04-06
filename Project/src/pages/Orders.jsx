import React, { useContext, useState, useMemo } from "react";
import { AuthContext } from "../context/AuthContext.jsx";
import { useNavigate } from "react-router-dom";
import { motion, AnimatePresence } from "framer-motion";

/**
 * Orders.jsx (~400 lines)
 * Professional order management with:
 * - View all buyer orders
 * - Order details and items
 * - Delivery status tracking
 * - Timeline of order events
 * - Reorder and cancel functionality
 * - Order filtering and sorting
 * - Responsive design with animations
 */

export default function Orders() {
  const { user } = useContext(AuthContext);
  const navigate = useNavigate();

  // Get orders from localStorage
  const [filterStatus, setFilterStatus] = useState("all");
  const [sortBy, setSortBy] = useState("newest");
  const [selectedOrder, setSelectedOrder] = useState(null);

  // Load orders from localStorage
  const allOrders = useMemo(() => {
    const orders = JSON.parse(localStorage.getItem("fc_orders") || "[]");
    return orders.filter(o => o.buyerEmail === user?.email) || [];
  }, [user?.email]);

  // Filter orders
  const filteredOrders = useMemo(() => {
    let result = allOrders;

    // Filter by status
    if (filterStatus !== "all") {
      result = result.filter(o => o.status === filterStatus);
    }

    // Sort
    if (sortBy === "newest") {
      result = [...result].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
    } else if (sortBy === "oldest") {
      result = [...result].sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));
    } else if (sortBy === "amount-high") {
      result = [...result].sort((a, b) => parseFloat(b.summary.total) - parseFloat(a.summary.total));
    } else if (sortBy === "amount-low") {
      result = [...result].sort((a, b) => parseFloat(a.summary.total) - parseFloat(b.summary.total));
    }

    return result;
  }, [allOrders, filterStatus, sortBy]);

  /**
   * Get status color and icon
   */
  function getStatusStyle(status) {
    const styles = {
      placed: { bg: "bg-blue-100", text: "text-blue-800", icon: "📦", label: "Order Placed" },
      processing: { bg: "bg-yellow-100", text: "text-yellow-800", icon: "⚙️", label: "Processing" },
      shipped: { bg: "bg-purple-100", text: "text-purple-800", icon: "🚚", label: "Shipped" },
      delivered: { bg: "bg-green-100", text: "text-green-800", icon: "✅", label: "Delivered" },
      cancelled: { bg: "bg-red-100", text: "text-red-800", icon: "❌", label: "Cancelled" }
    };
    return styles[status] || styles.placed;
  }

  /**
   * Get delivery progress steps
   */
  function getDeliverySteps(status) {
    const steps = [
      { label: "Order Placed", status: "placed" },
      { label: "Processing", status: "processing" },
      { label: "Shipped", status: "shipped" },
      { label: "Delivered", status: "delivered" }
    ];

    const statusMap = { placed: 0, processing: 1, shipped: 2, delivered: 3, cancelled: -1 };
    const currentStep = statusMap[status] || 0;

    return steps.map((step, index) => ({
      ...step,
      completed: index < currentStep,
      current: index === currentStep,
      pending: index > currentStep
    }));
  }

  /**
   * Handle reorder
   */
  function handleReorder(order) {
    if (window.confirm("Add all items from this order to cart?")) {
      // Add items to cart
      const cartItems = JSON.parse(localStorage.getItem("fc_cart") || "[]");
      order.items.forEach(item => {
        cartItems.push({
          ...item,
          cartItemId: Date.now() + Math.random()
        });
      });
      localStorage.setItem("fc_cart", JSON.stringify(cartItems));
      alert("Items added to cart!");
      navigate("/cart");
    }
  }

  /**
   * Handle cancel order
   */
  function handleCancelOrder(orderId) {
    if (window.confirm("Are you sure you want to cancel this order?")) {
      const orders = JSON.parse(localStorage.getItem("fc_orders") || "[]");
      const updated = orders.map(o =>
        o.id === orderId ? { ...o, status: "cancelled" } : o
      );
      localStorage.setItem("fc_orders", JSON.stringify(updated));
      alert("Order cancelled successfully!");
      window.location.reload();
    }
  }

  /**
   * Calculate order statistics
   */
  const stats = useMemo(() => {
    return {
      total: allOrders.length,
      placed: allOrders.filter(o => o.status === "placed").length,
      processing: allOrders.filter(o => o.status === "processing").length,
      shipped: allOrders.filter(o => o.status === "shipped").length,
      delivered: allOrders.filter(o => o.status === "delivered").length,
      cancelled: allOrders.filter(o => o.status === "cancelled").length,
      totalSpent: allOrders.reduce((sum, o) => sum + parseFloat(o.summary.total), 0)
    };
  }, [allOrders]);

  const statuses = ["all", "placed", "processing", "shipped", "delivered", "cancelled"];

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-purple-900 to-slate-900">
      {/* Header */}
      <div className="bg-gradient-to-r from-purple-700 via-pink-600 to-red-600 text-white p-6 shadow-xl sticky top-0 z-30">
        <div className="max-w-6xl mx-auto">
          <div className="flex items-center justify-between mb-4">
            <button
              onClick={() => navigate("/buyer-dashboard")}
              className="hover:bg-white/20 px-4 py-2 rounded-lg transition flex items-center gap-2"
            >
              ← Back to Shopping
            </button>
            <h1 className="text-3xl font-bold">My Orders</h1>
            <div className="text-right">
              <p className="text-pink-100">Buyer: {user?.name}</p>
            </div>
          </div>

          {/* Stats */}
          <div className="grid grid-cols-2 md:grid-cols-6 gap-2">
            <div className="bg-white/10 backdrop-blur px-3 py-2 rounded-lg text-center text-sm">
              <p className="text-pink-100">Total Orders</p>
              <p className="text-2xl font-bold">{stats.total}</p>
            </div>
            <div className="bg-white/10 backdrop-blur px-3 py-2 rounded-lg text-center text-sm">
              <p className="text-pink-100">Delivered</p>
              <p className="text-2xl font-bold text-green-300">{stats.delivered}</p>
            </div>
            <div className="bg-white/10 backdrop-blur px-3 py-2 rounded-lg text-center text-sm">
              <p className="text-pink-100">Shipped</p>
              <p className="text-2xl font-bold text-blue-300">{stats.shipped}</p>
            </div>
            <div className="bg-white/10 backdrop-blur px-3 py-2 rounded-lg text-center text-sm">
              <p className="text-pink-100">Processing</p>
              <p className="text-2xl font-bold text-yellow-300">{stats.processing}</p>
            </div>
            <div className="bg-white/10 backdrop-blur px-3 py-2 rounded-lg text-center text-sm">
              <p className="text-pink-100">Cancelled</p>
              <p className="text-2xl font-bold text-red-300">{stats.cancelled}</p>
            </div>
            <div className="bg-white/10 backdrop-blur px-3 py-2 rounded-lg text-center text-sm">
              <p className="text-pink-100">Total Spent</p>
              <p className="text-2xl font-bold">₹{stats.totalSpent.toLocaleString()}</p>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-6xl mx-auto p-6">
        {/* Filters */}
        <div className="bg-white rounded-lg shadow-lg p-6 mb-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Status filter */}
            <div>
              <label className="block text-sm font-semibold mb-3 text-gray-700">Filter by Status</label>
              <div className="flex flex-wrap gap-2">
                {statuses.map(status => (
                  <button
                    key={status}
                    onClick={() => setFilterStatus(status)}
                    className={`px-4 py-2 rounded-lg font-semibold transition ${
                      filterStatus === status
                        ? "bg-purple-600 text-white shadow-lg"
                        : "bg-gray-100 text-gray-800 hover:bg-gray-200"
                    }`}
                  >
                    {status.charAt(0).toUpperCase() + status.slice(1)}
                  </button>
                ))}
              </div>
            </div>

            {/* Sort by */}
            <div>
              <label className="block text-sm font-semibold mb-3 text-gray-700">Sort By</label>
              <select
                value={sortBy}
                onChange={e => setSortBy(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
              >
                <option value="newest">Newest First</option>
                <option value="oldest">Oldest First</option>
                <option value="amount-high">Highest Amount</option>
                <option value="amount-low">Lowest Amount</option>
              </select>
            </div>
          </div>
        </div>

        {/* Orders list */}
        {filteredOrders.length === 0 ? (
          <motion.div
            className="bg-white rounded-lg shadow-lg p-12 text-center"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
          >
            <p className="text-gray-500 text-xl mb-4">No orders found</p>
            <p className="text-gray-400 mb-6">
              {filterStatus === "all"
                ? "You haven't placed any orders yet."
                : `No orders with status "${filterStatus}"`}
            </p>
            <button
              onClick={() => navigate("/buyer-dashboard")}
              className="bg-purple-600 hover:bg-purple-700 text-white px-8 py-3 rounded-lg font-bold transition"
            >
              Start Shopping
            </button>
          </motion.div>
        ) : (
          <div className="space-y-6">
            {filteredOrders.map((order, idx) => {
              const statusStyle = getStatusStyle(order.status);
              const steps = getDeliverySteps(order.status);

              return (
                <motion.div
                  key={order.id}
                  className="bg-white rounded-lg shadow-lg hover:shadow-xl transition overflow-hidden"
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: idx * 0.05 }}
                >
                  {/* Order header */}
                  <div className={`${statusStyle.bg} ${statusStyle.text} p-6 border-l-4 border-current`}>
                    <div className="flex items-center justify-between flex-wrap gap-4 mb-3">
                      <div className="flex items-center gap-3">
                        <span className="text-3xl">{statusStyle.icon}</span>
                        <div>
                          <p className="text-sm font-semibold">Order ID</p>
                          <p className="text-xl font-bold">{order.id}</p>
                        </div>
                      </div>
                      <div className="text-right">
                        <p className="text-sm font-semibold">Status</p>
                        <p className="text-xl font-bold">{statusStyle.label}</p>
                      </div>
                      <div className="text-right">
                        <p className="text-sm font-semibold">Date</p>
                        <p className="text-lg">
                          {new Date(order.createdAt).toLocaleDateString()}
                        </p>
                      </div>
                    </div>
                  </div>

                  {/* Delivery timeline */}
                  <div className="p-6 border-b bg-gray-50">
                    <p className="text-sm font-semibold text-gray-700 mb-3">Delivery Status</p>
                    <div className="flex gap-2 overflow-x-auto">
                      {steps.map((step, i) => (
                        <div key={step.status} className="flex items-center">
                          <div className={`flex flex-col items-center min-w-max`}>
                            <div
                              className={`w-10 h-10 rounded-full flex items-center justify-center font-bold transition ${
                                step.completed
                                  ? "bg-green-500 text-white"
                                  : step.current
                                  ? "bg-blue-500 text-white ring-2 ring-blue-300"
                                  : "bg-gray-300 text-gray-600"
                              }`}
                            >
                              {step.completed ? "✓" : i + 1}
                            </div>
                            <p className="text-xs text-gray-600 mt-2 text-center w-20">{step.label}</p>
                          </div>
                          {i < steps.length - 1 && (
                            <div className={`h-1 w-8 mx-2 ${step.completed ? "bg-green-500" : "bg-gray-300"}`} />
                          )}
                        </div>
                      ))}
                    </div>
                  </div>

                  {/* Order items */}
                  <div className="p-6 border-b">
                    <p className="text-sm font-semibold text-gray-700 mb-3">Items ({order.items.length})</p>
                    <div className="space-y-3">
                      {order.items.map(item => (
                        <div key={item.cartItemId} className="flex gap-4 p-3 bg-gray-50 rounded-lg">
                          {item.image && (
                            <img
                              src={item.image}
                              alt={item.name}
                              className="w-16 h-16 object-cover rounded"
                            />
                          )}
                          <div className="flex-1">
                            <p className="font-semibold text-gray-800">{item.name}</p>
                            <p className="text-sm text-gray-600">By {item.sellerName}</p>
                            <div className="flex justify-between mt-2">
                              <span className="text-sm">₹{item.price} × {item.quantity}</span>
                              <span className="font-bold">₹{(item.price * item.quantity).toFixed(2)}</span>
                            </div>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>

                  {/* Order summary */}
                  <div className="p-6 bg-gradient-to-r from-slate-50 to-slate-100">
                    <div className="flex justify-between items-center mb-4">
                      <div>
                        <p className="text-gray-600">Subtotal</p>
                        <p className="font-semibold">₹{order.summary.subtotal}</p>
                      </div>
                      <div>
                        <p className="text-gray-600">Shipping</p>
                        <p className="font-semibold">
                          {order.summary.shippingCost === 0 ? "FREE" : `₹${order.summary.shippingCost}`}
                        </p>
                      </div>
                      <div>
                        <p className="text-gray-600">Tax</p>
                        <p className="font-semibold">₹{order.summary.tax}</p>
                      </div>
                      <div className="border-l pl-4">
                        <p className="text-gray-600">Total</p>
                        <p className="text-2xl font-bold text-purple-600">₹{order.summary.total}</p>
                      </div>
                    </div>

                    {/* Actions */}
                    <div className="flex gap-3">
                      {order.status === "placed" && (
                        <button
                          onClick={() => handleCancelOrder(order.id)}
                          className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-lg font-semibold transition"
                        >
                          Cancel Order
                        </button>
                      )}
                      <button
                        onClick={() => handleReorder(order)}
                        className="bg-indigo-500 hover:bg-indigo-600 text-white px-4 py-2 rounded-lg font-semibold transition"
                      >
                        Reorder
                      </button>
                      <button
                        onClick={() => setSelectedOrder(order.id === selectedOrder ? null : order.id)}
                        className="bg-gray-500 hover:bg-gray-600 text-white px-4 py-2 rounded-lg font-semibold transition"
                      >
                        {selectedOrder === order.id ? "Hide" : "Details"}
                      </button>
                    </div>
                  </div>
                </motion.div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
}
