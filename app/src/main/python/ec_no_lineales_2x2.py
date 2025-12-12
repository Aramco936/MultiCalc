import sympy as sp

def newton_raphson_2x2(funcion1, funcion2,literal1, literal2,x0, y0,tolerancia, iter_max):
    try:
        x = sp.symbols(literal1)
        y = sp.symbols(literal2)

        f1 = sp.sympify(funcion1.replace("^", "**"))
        f2 = sp.sympify(funcion2.replace("^", "**"))

        F = sp.Matrix([f1, f2])
        V = sp.Matrix([x, y])
        J = F.jacobian(V)

        xi = float(x0)
        yi = float(y0)

        for _ in range(iter_max):
            subs = {x: xi, y: yi}
            J_eval = J.subs(subs)

            if J_eval.det() == 0:
                raise ValueError("Jacobiano singular")

            F_eval = sp.Matrix([f1.subs(subs), f2.subs(subs)])
            Xk = sp.Matrix([xi, yi])
            Xk1 = Xk - J_eval.inv() * F_eval

            error = max(abs(Xk1[0] - xi), abs(Xk1[1] - yi))
            xi, yi = float(Xk1[0]), float(Xk1[1])

            if error < tolerancia:
                break

        return [xi, yi]

    except Exception as e:
        return [-999999.0, -999999.0]  # error detectable en Android