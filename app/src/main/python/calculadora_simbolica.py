import sympy as sp


def derivar(expresion,literal):
    try:
        literal_sympificada = sp.symbols(literal)
        expresion_sympificada = sp.sympify(expresion)
        #return sp.latex(sp.simplify(sp.diff(expresion_sympificada,literal_sympificada)))
        return sp.simplify(sp.diff(expresion_sympificada,literal_sympificada))
    except Exception as Error_entrada:
        return False

def integrar(expresion,literal):
    try:
        literal_sympificada = sp.symbols(literal)
        expresion_sympificada = sp.sympify(expresion)
        #return sp.latex(sp.integrate(expresion_sympificada,literal_sympificada))
        return sp.simplify(sp.integrate(expresion_sympificada,literal_sympificada))
    except Exception as Error_entrada_integral:
        return False