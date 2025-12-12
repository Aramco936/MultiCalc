'''
import sympy as sp
import math

def newton_raphson(funcion, literal, aprox_inicial, error_permitido, max_iter):
    try:
        x = sp.symbols(literal)
        expr = sp.sympify(funcion)

        # Calcular la función y su derivada
        f = sp.lambdify(x, expr, "math")
        derivada = sp.diff(expr, x)
        df = sp.lambdify(x, derivada, "math")

        xi = float(aprox_inicial)
        tol = float(error_permitido)
        max_iter = int(max_iter)

        resultados = []

        for i in range(1, max_iter + 1):
            fxi = f(xi)
            dfxi = df(xi)

            # Verificar que la derivada no sea cero
            if abs(dfxi) < 1e-15:
                return {
                    "ok": False,
                    "message": "La derivada es cero. No se puede continuar.",
                    "data": []
                }

            # Calcular siguiente aproximación
            xi1 = xi - fxi / dfxi

            # Calcular error
            if xi1 != 0:
                error_actual = abs((xi1 - xi) / xi1)
            else:
                error_actual = abs(xi1 - xi)

            resultados.append({
                "i": i,
                "xi": xi,
                "fxi": fxi,
                "dfxi": dfxi,
                "xi1": xi1,  # Siguiente aproximación
                "error": error_actual
            })

            # Verificar criterio de parada
            if error_actual <= tol:
                break

            # Actualizar para siguiente iteración
            xi = xi1

        return {
            "ok": True,
            "message": f"Convergencia alcanzada en {len(resultados)} iteraciones",
            "data": resultados
        }

    except Exception as e:
        return {
            "ok": False,
            "message": str(e),
            "data": []
        }
'''
import sympy as sp
import math

def newton_raphson(funcion, literal, x0, tol, max_iter):
    try:
        x = sp.symbols(literal)
        expr = sp.sympify(funcion.replace("^", "**"))
        d_expr = sp.diff(expr, x)

        f = sp.lambdify(x, expr, "math")
        df = sp.lambdify(x, d_expr, "math")

        data = []
        xi = float(x0)

        for i in range(1, max_iter + 1):
            fxi = f(xi)
            dfxi = df(xi)

            if dfxi == 0:
                return {
                    "ok": False,
                    "message": "Derivada cero, no se puede continuar",
                    "data": []
                }

            xi1 = xi - fxi / dfxi
            error = abs(xi1 - xi)

            data.append({
                "i": i,
                "xi": xi,
                "fxi": fxi,
                "xi1": xi1,
                "error": error
            })

            if error < tol:
                break

            xi = xi1

        return {
            "ok": True,
            "message": "Cálculo exitoso",
            "data": data
        }

    except Exception as e:
        return {
            "ok": False,
            "message": str(e),
            "data": []
        }
