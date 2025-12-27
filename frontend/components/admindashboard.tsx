import React, { useState } from 'react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { Calendar, DollarSign, TrendingUp, Users, Package, ShoppingCart, Building2 } from 'lucide-react';
import axios from 'axios';
import { redirect } from 'next/navigation';
import { fetchBookReport, fetchTopBooks, fetchTopCustomers, fetchPreviousMonthSales, fetchDailySales,getAllbooks } from '@/api/reports/reports';
import { useRouter } from 'next/navigation';
import { Button } from '@/components/ui/button';
import { getAccessToken } from '@/lib/token-storage';
const AdminDashboard = () => {
  const router = useRouter();
  const [selectedDate, setSelectedDate] = useState('');
  const [selectedBook, setSelectedBook] = useState('');
   const [bookOrders, setBookOrders] = React.useState<any>(null);
   const [topBooks,setTopBooks] = useState<any[]>([]);
   const [topCustomers, setTopCustomers] = useState<any>(null);
   const [previousMonthSales, setPreviousMonthSales] = useState<any>(null);
   const [allBooks, setallBooks] = useState<any>(null);
   const [dailySales, setDailySales] = useState<any>(null);
   const [errors, setErrors] = useState<{[key: string]: string}>({});
   const token = getAccessToken()||"";
  React.useEffect(() => {
        const getbooks = async () => {
            try {

                const data = await getAllbooks(
                    token
                );
                console.log("Fetched asankjns:", data);

                setallBooks(data);
            } catch (err: any) {
                if (err.message === "FORBIDDEN") {
                    setErrors(prev => ({ ...prev, bookOrders: "You do not have permission to view this data." }));
                } else if (err.message === "UNAUTHORIZED") {
                    redirect("/login");
                } else {
                    setErrors(prev => ({ ...prev, bookOrders: "Unexpected server error." }));
                }
            }
        };
        getbooks();
    }, [token]);
  React.useEffect(() => {
        const loadReport = async () => {
            try {
                const data = await fetchBookReport(
                    selectedBook,
                    token
                );
                console.log("Fetched orders:", data);
                setBookOrders(data);
                setErrors(prev => ({ ...prev, bookOrders: '' }));
            } catch (err: any) {
                if (err.message === "FORBIDDEN") {
                    setErrors(prev => ({ ...prev, bookOrders: "You do not have permission to view this data." }));
                } else if (err.message === "UNAUTHORIZED") {
                    redirect("/login");
                } else {
                    setErrors(prev => ({ ...prev, bookOrders: "Unexpected server error." }));
                }
            }
        };

        loadReport();
    }, [token,selectedBook]);

      React.useEffect(() => {
        const loadReport = async () => {
            try {
                const data = await fetchTopBooks(
                    token
                );
                console.log("Fetched orders:", data);
                setTopBooks(Array.isArray(data) ? data : []);
            } catch (err: any) {
                if (err.message === "FORBIDDEN") {
                    setErrors(prev => ({ ...prev, topBooks: "You do not have permission to view this data." }));
                } else if (err.message === "UNAUTHORIZED") {
                    redirect("/login");
                } else {
                    setErrors(prev => ({ ...prev, topBooks: "Unexpected server error." }));
                }
            }
        };

        loadReport();
    }, [token]);

      React.useEffect(() => {
        const loadTopCustomers = async () => {
            try {
                const data = await fetchTopCustomers(
                    token
                );
                console.log("Fetched top customers:", data);
                setTopCustomers(data);
                setErrors(prev => ({ ...prev, topCustomers: '' }));
            } catch (err: any) {
                if (err.message === "FORBIDDEN") {
                    setErrors(prev => ({ ...prev, topCustomers: "You do not have permission to view this data." }));
                } else if (err.message === "UNAUTHORIZED") {
                    redirect("/login");
                } else {
                    setErrors(prev => ({ ...prev, topCustomers: "Unexpected server error." }));
                }
            }
        };

        loadTopCustomers();
    }, [token]);

      React.useEffect(() => {
        const loadPreviousMonthSales = async () => {
            try {
                const data = await fetchPreviousMonthSales(
                    token
                );
                console.log("Fetched previous month sales:", data);
                setPreviousMonthSales(data);
                setErrors(prev => ({ ...prev, previousMonthSales: '' }));
            } catch (err: any) {
                if (err.message === "FORBIDDEN") {
                    setErrors(prev => ({ ...prev, previousMonthSales: "You do not have permission to view this data." }));
                } else if (err.message === "UNAUTHORIZED") {
                    redirect("/login");
                } else {
                    setErrors(prev => ({ ...prev, previousMonthSales: "Unexpected server error." }));
                }
            }
        };

        loadPreviousMonthSales();
    }, [token]);

      React.useEffect(() => {
        const loadDailySales = async () => {
            if (selectedDate) {
                try {
                    const data = await fetchDailySales(
                        selectedDate,
                        token
                    );
                    console.log("Fetched daily sales:", data);
                    setDailySales(data);
                    setErrors(prev => ({ ...prev, dailySales: '' }));
                } catch (err: any) {
                    if (err.message === "FORBIDDEN") {
                        setErrors(prev => ({ ...prev, dailySales: "You do not have permission to view this data." }));
                    } else if (err.message === "UNAUTHORIZED") {
                        redirect("/login");
                    } else {
                        setErrors(prev => ({ ...prev, dailySales: "Unexpected server error." }));
                    }
                }
            }
        };

        loadDailySales();
    }, [selectedDate, token]);

  const previousMonthSalesData = previousMonthSales || {
    totalSales: 45780.50,
    totalOrders: 342,
    month: 'November 2024'
  };

  const dailySalesData = dailySales || []


  const topCustomersData = Array.isArray(topCustomers) ? topCustomers : [
    { rank: 1, name: 'Sarah Johnson', totalPurchases: 1250.00, orders: 12 },
    { rank: 2, name: 'Michael Chen', totalPurchases: 980.50, orders: 8 },
    { rank: 3, name: 'Emily Rodriguez', totalPurchases: 875.25, orders: 10 },
    { rank: 4, name: 'David Kim', totalPurchases: 720.00, orders: 6 },
    { rank: 5, name: 'Jessica Williams', totalPurchases: 695.75, orders: 7 }
  ];


  const topBooks2 = [
    { rank: 1, title: 'Learning Python', isbn: '9781491950357', category: 'Science', copiesSold: 145 },
    { rank: 2, title: 'Head First Java', isbn: '9780596007973', category: 'Science', copiesSold: 132 },
    { rank: 3, title: 'The Martian', isbn: '9780143127550', category: 'Science', copiesSold: 118 },
    { rank: 4, title: 'To Kill a Mockingbird', isbn: '9780062315007', category: 'Art', copiesSold: 105 },
    { rank: 5, title: 'History Unveiled', isbn: '9780000000001', category: 'History', copiesSold: 98 },
    { rank: 6, title: 'Tech Revolution', isbn: '9780000000002', category: 'Technology', copiesSold: 87 },
    { rank: 7, title: 'Art of Living', isbn: '9780000000003', category: 'Art', copiesSold: 76 },
    { rank: 8, title: 'Digital Future', isbn: '9780000000004', category: 'Technology', copiesSold: 69 },
    { rank: 9, title: 'Garden Guide', isbn: '9780000000005', category: 'Nature', copiesSold: 64 },
    { rank: 10, title: 'Fitness Journey', isbn: '9780000000006', category: 'Health', copiesSold: 58 }
  ];



  const booksList = Array.isArray(topBooks) ? topBooks.map((book:any) => book.title) : [];

  const chartData = Array.isArray(topBooks) ? topBooks.slice(0, 5).map((book:any) => ({
    name: book.book?.title?.length > 15 ? book.book?.title.substring(0, 15) + '...' : book.book?.title,
    copies: book.quantity
  })) : [];

  return (
    <div className="min-h-screen bg-gray-50 p-6">
      <div className="max-w-7xl mx-auto">
        <div className="flex justify-between items-center mb-8">
          <h1 className="text-3xl font-bold text-gray-800">Admin Reports Dashboard</h1>
          <div className="flex gap-2">
            <Button
              variant="outline"
              onClick={() => router.push("/admin_dashboard/users")}
              className="flex items-center gap-2"
            >
              <Users className="h-4 w-4" />
              Manage Users
            </Button>
            <Button
              variant="outline"
              onClick={() => router.push("/books")}
              className="flex items-center gap-2"
            >
              <Package className="h-4 w-4" />
              Manage Books
            </Button>
            <Button
              variant="outline"
              onClick={() => router.push("/publishers")}
              className="flex items-center gap-2"
            >
              <Building2 className="h-4 w-4" />
              Manage Publishers
            </Button>
            <Button
              variant="outline"
              onClick={() => router.push("/publisher-orders")}
              className="flex items-center gap-2"
            >
              <ShoppingCart className="h-4 w-4" />
              Manage Publisher Orders
            </Button>
          </div>
        </div>

        {/* Report 1: Previous Month Sales */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <div className="flex items-center mb-4">
            <DollarSign className="w-6 h-6 text-blue-600 mr-2" />
            <h2 className="text-xl font-semibold text-gray-800">Total Sales - Previous Month</h2>
          </div>
          {errors.previousMonthSales && (
            <div className="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded">
              {errors.previousMonthSales}
            </div>
          )}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="bg-blue-50 p-4 rounded-lg">
              <p className="text-sm text-gray-600">Month</p>
              <p className="text-2xl font-bold text-blue-600">{previousMonthSalesData.month}</p>
            </div>
            <div className="bg-green-50 p-4 rounded-lg">
              <p className="text-sm text-gray-600">Total Sales</p>
              <p className="text-2xl font-bold text-green-600">${previousMonthSalesData.totalSales?.toFixed(2)}</p>
            </div>
            <div className="bg-purple-50 p-4 rounded-lg">
              <p className="text-sm text-gray-600">Total Orders</p>
              <p className="text-2xl font-bold text-purple-600">{previousMonthSalesData.totalOrders}</p>
            </div>
          </div>
        </div>

        {/* Report 2: Sales on Specific Day */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <div className="flex items-center mb-4">
            <Calendar className="w-6 h-6 text-indigo-600 mr-2" />
            <h2 className="text-xl font-semibold text-gray-800">Sales on Specific Day</h2>
          </div>
          {errors.dailySales && (
            <div className="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded">
              {errors.dailySales}
            </div>
          )}
          <div className="mb-4">
            <label className="block text-sm font-medium text-gray-700 mb-2">Select Date:</label>
            <input
              type="date"
              value={selectedDate}
              onChange={(e) => setSelectedDate(e.target.value)}
              className="border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
          </div>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
            <div className="bg-indigo-50 p-4 rounded-lg">
              <p className="text-sm text-gray-600">Total Sales</p>
              <p className="text-2xl font-bold text-indigo-600">${Array.isArray(dailySalesData) 
                ? dailySalesData.reduce((total: number, item: any) => total + (item.totalprofit || 0), 0)?.toFixed(2) 
                : (dailySalesData?.totalSales || 0)?.toFixed(2) || '0.00'}</p>
            </div>
            <div className="bg-pink-50 p-4 rounded-lg">
              <p className="text-sm text-gray-600">Total Books Sold</p>
              <p className="text-2xl font-bold text-pink-600">{Array.isArray(dailySalesData) 
                ? dailySalesData.reduce((total: number, item: any) => total + (item.quantity || 0), 0) 
                : (dailySalesData?.totalOrders || 0)}</p>
            </div>
          </div>
          {Array.isArray(dailySalesData) && dailySalesData.length > 0 && (
            <div className="overflow-x-auto">
              <table className="min-w-full">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ISBN</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Book Title</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Quantity Sold</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Total Profit</th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {dailySalesData.map((item: any, index: number) => (
                    <tr key={item.book?.isbn || index} className="hover:bg-gray-50">
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{item.book?.isbn}</td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{item.book?.title}</td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{item.quantity}</td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">${item.totalprofit?.toFixed(2)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>

        {/* Report 3: Top 5 Customers */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <div className="flex items-center mb-4">
            <Users className="w-6 h-6 text-orange-600 mr-2" />
            <h2 className="text-xl font-semibold text-gray-800">Top 5 Customers (Last 3 Months)</h2>
          </div>
          {errors.topCustomers && (
            <div className="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded">
              {errors.topCustomers}
            </div>
          )}
          <div className="overflow-x-auto">
            <table className="min-w-full">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Rank</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Customer Name</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Number of items</th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {topCustomersData.map((customer:any, index:any) => (
                  <tr key={customer.rank || index + 1} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className="text-lg font-bold text-orange-600">#{customer.rank || index + 1}</span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{customer.user?.firstName || customer.customerName}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{customer.numberoforders || customer.orderCount}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Report 4: Top 10 Selling Books */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <div className="flex items-center mb-4">
            <TrendingUp className="w-6 h-6 text-teal-600 mr-2" />
            <h2 className="text-xl font-semibold text-gray-800">Top 10 Selling Books (Last 3 Months)</h2>
          </div>
          {errors.topBooks && (
            <div className="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded">
              {errors.topBooks}
            </div>
          )}
          <div className="mb-6">
            <ResponsiveContainer width="100%" height={250}>
              <BarChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" angle={-45} textAnchor="end" height={100} />
                <YAxis />
                <Tooltip />
                <Legend />
                <Bar dataKey="copies" fill="#14b8a6" name="Copies Sold" />
              </BarChart>
            </ResponsiveContainer>
          </div>
          <div className="overflow-x-auto">
            <table className="min-w-full">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Rank</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ISBN</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Book Title</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Copies Sold</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Total Profit</th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {Array.isArray(topBooks) && topBooks.length > 0 ? (
                  topBooks.map((book:any,idx :number) => (
                    <tr key={book.book?.isbn || idx} className="hover:bg-gray-50">
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className="text-lg font-bold text-teal-600">#{idx+1}</span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{book.book?.isbn}</td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{book.book?.title}</td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{book.quantity}</td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">${book.totalprofit?.toFixed(2)}</td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan={5} className="px-6 py-4 text-center text-gray-500">
                      No data available
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>

        {/* Report 5: Book Order Count */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <div className="flex items-center mb-4">
            <Package className="w-6 h-6 text-red-600 mr-2" />
            <h2 className="text-xl font-semibold text-gray-800">Book Replenishment Orders</h2>
          </div>
          {errors.bookOrders && (
            <div className="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded">
              {errors.bookOrders}
            </div>
          )}
          <div className="mb-4">
            <label className="block text-sm font-medium text-gray-700 mb-2">Select Book:</label>
            <select
              value={selectedBook}
              onChange={(e) => setSelectedBook(e.target.value)}
              className="border border-gray-300 rounded-lg px-4 py-2 w-full md:w-96 focus:outline-none focus:ring-2 focus:ring-red-500"
            >
              <option value="">Choose a book...</option>
              {allBooks?.map((book: any, index: number) => (
                <option key={index} value={book.isbn}>{book.title}</option>
              ))}
            </select>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="bg-red-50 p-4 rounded-lg">
              <p className="text-sm text-gray-600">Times Ordered (Replenishment)</p>
              <p className="text-2xl font-bold text-red-600">{bookOrders?.numberoforders ?? 0}</p>
            </div>
            <div className="bg-amber-50 p-4 rounded-lg">
              <p className="text-sm text-gray-600">Amount ordered</p>
              <p className="text-2xl font-bold text-amber-600">{(bookOrders?.numberoforders ?? 0) * 50}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboard;