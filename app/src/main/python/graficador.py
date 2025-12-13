'''
import sympy as sp
import matplotlib.pyplot as plt
import numpy as np

def graficar_archivo(expresion, literal, lim_inferior, lim_superior, ruta_archivo):
    try:
        plt.switch_backend('Agg')

        dominio = np.arange(lim_inferior, lim_superior, 0.01)
        expresion_limpia = expresion.replace('^', '**')
        x = sp.symbols(literal)
        expresion_sympificada = sp.sympify(expresion_limpia)

        f = sp.lambdify(x, expresion_sympificada, 'numpy')
        codominio = f(dominio)

        plt.figure(figsize=(8, 6))
        plt.plot(dominio, codominio, 'b-', linewidth=2.5)
        plt.title(f'f({literal}) = {expresion}')
        plt.grid(True)

        # Guardar en archivo
        plt.savefig(ruta_archivo, format='png', dpi=100)
        plt.close()

        return ruta_archivo

    except Exception as e:
        return False

'''
import sympy as sp
import numpy as np
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

def graficar_archivo(expresion, literal, lim_inferior, lim_superior, ruta_archivo):
    try:
        x = sp.symbols(literal)
        expr = sp.sympify(expresion.replace("^", "**"))

        xs = np.arange(lim_inferior, lim_superior, 0.01)
        ys = [expr.subs(x, val) for val in xs]

        plt.figure()
        plt.plot(xs, ys)
        plt.grid(True)
        plt.xlabel(literal)
        plt.ylabel("f(x)")
        plt.title(f"f(x) = {expresion}")

        # Guardar imagen en la ruta recibida de Android
        plt.savefig(ruta_archivo)
        plt.close()

        return True
    except Exception as e:
        print("ERROR:", e)
        return False
