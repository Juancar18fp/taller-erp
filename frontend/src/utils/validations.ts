export const required = (val: string | null) => !!val || "Campo obligatorio";

export const validarPrecio = (val: string | null) => {
  const precio = parseFloat(val ?? "");
  if (isNaN(precio) || precio < 0) return "Debe ser un número válido mayor o igual a 0";
  return true;
};

export const validarStock = (val: string | null) => {
  const stock = parseInt(val ?? "");
  if (isNaN(stock) || stock < 0 || !Number.isInteger(parseFloat(val ?? ""))) {
    return "Debe ser un número entero mayor o igual a 0";
  }
  return true;
};

export const validEmail = (val: string | null) => {
  if (!val) return true;
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val) || "Email inválido";
};

export const validDocument = (val: string | null) => {
  if (!val) return true;
  const dniRegex = /^[XYZ]?\d{7,8}[A-Z]$/i;
  const cifRegex = /^[A-HJNPQRSUVW]\d{7}[0-9A-J]$/i;
  return dniRegex.test(val) || cifRegex.test(val) || "Formato inválido";
};

export const validPhone = (val: string | null) => {
  if (!val) return true;
  return /^[6-9]\d{8}$/.test(val) || "Teléfono inválido";
};

export const validSSN = (val: string | null) => {
  if (!val) return true;
  return /^\d{12}$/.test(val) || "Número de Seguridad Social inválido";
};

export const validSalary = (val: number | null | undefined) => {
  if (val === null || val === undefined || val === 0) return "Salario requerido";
  return val > 0 || "El salario debe ser mayor a 0";
};

export const validIBAN = (val: string | null) => {
  if (!val) return true;
  const cleanIban = val.replace(/\s/g, "");
  const ibanRegex = /^ES\d{22}$/;
  return ibanRegex.test(cleanIban) || "IBAN inválido";
};
