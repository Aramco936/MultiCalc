import sympy as sp
import math

def biseccion(funcion, literal, a, b, max_iter, tol):
    try:
        x = sp.symbols(literal)
        expr = sp.sympify(funcion)
        f = sp.lambdify(x, expr, "math")

        a = float(a)
        b = float(b)
        tol = float(tol)
        max_iter = int(max_iter)

        fa = f(a)
        fb = f(b)

        if fa * fb > 0:
            return {
                "ok": False,
                "message": "El intervalo no encierra una raíz única",
                "data": []
            }

        resultados = []

        for i in range(1, max_iter + 1):
            m = (a + b) / 2
            fm = f(m)
            error = abs(b - a) / 2

            resultados.append({
                "i": i,
                "a": a,
                "b": b,
                "fa": fa,
                "fb": fb,
                "m": m,
                "fm": fm,
                "error": error
            })

            if abs(fm) < tol or error < tol:
                break

            if fa * fm < 0:
                b = m
                fb = fm
            else:
                a = m
                fa = fm

        return {
            "ok": True,
            "message": "OK",
            "data": resultados
        }

    except Exception as e:
        return {
            "ok": False,
            "message": str(e),
            "data": []
        }
