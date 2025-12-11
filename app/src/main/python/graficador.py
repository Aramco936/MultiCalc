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