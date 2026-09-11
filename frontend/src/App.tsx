import { Route, Routes } from "react-router-dom";
import { Layout } from "./components/layout/Layout";
import { Home } from "./pages/Home";
import { Catalog } from "./pages/Catalog";
import { ProductDetail } from "./pages/ProductDetail";
import { NotFound } from "./pages/NotFound";
import { Login } from "./admin/pages/Login";
import { AdminLayout } from "./admin/AdminLayout";
import { ProtectedRoute } from "./admin/ProtectedRoute";
import { Dashboard } from "./admin/pages/Dashboard";
import { Categories } from "./admin/pages/Categories";
import { ProductsList } from "./admin/pages/ProductsList";
import { ProductForm } from "./admin/pages/ProductForm";
import { Inventory } from "./admin/pages/Inventory";

export function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route path="/" element={<Home />} />
        <Route path="/catalogo" element={<Catalog />} />
        <Route path="/produtos/:id" element={<ProductDetail />} />
        <Route path="*" element={<NotFound />} />
      </Route>

      <Route path="/admin/login" element={<Login />} />
      <Route path="/admin" element={<ProtectedRoute />}>
        <Route element={<AdminLayout />}>
          <Route index element={<Dashboard />} />
          <Route path="produtos" element={<ProductsList />} />
          <Route path="produtos/novo" element={<ProductForm />} />
          <Route path="produtos/:id/editar" element={<ProductForm />} />
          <Route path="categorias" element={<Categories />} />
          <Route path="estoque" element={<Inventory />} />
        </Route>
      </Route>
    </Routes>
  );
}
