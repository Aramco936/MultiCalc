import json
import random
import sympy as sp
from sympy import sympify, simplify

class GeneradorPreguntas:
    """
    Sistema de banco de preguntas con cajas por dificultad
    - Cada tema tiene 3 cajas: Fácil, Medio, Difícil
    - Se seleccionan aleatoriamente de la caja correspondiente
    - Fácil de expandir: solo edita el JSON
    """

    def __init__(self, ruta_banco_json):
        """
        Inicializa el generador cargando el banco de preguntas
        ruta_banco_json: ruta al archivo JSON con las preguntas
        """
        self.banco = self._cargar_banco(ruta_banco_json)
        self.x = sp.symbols('x')

    def _cargar_banco(self, ruta):
        """Carga el banco de preguntas desde el archivo JSON"""
        try:
            with open(ruta, 'r', encoding='utf-8') as f:
                return json.load(f)
        except FileNotFoundError:
            print(f"ERROR: No se encontró el archivo {ruta}")
            return {}
        except json.JSONDecodeError as e:
            print(f"ERROR: Archivo JSON inválido - {e}")
            return {}

    def obtener_temas_disponibles(self):
        """Retorna lista de temas disponibles en el banco"""
        return list(self.banco.keys())

    def obtener_num_preguntas(self, tema, dificultad):
        """Retorna cuántas preguntas hay disponibles para un tema/dificultad"""
        try:
            return len(self.banco[tema][dificultad])
        except KeyError:
            return 0

    def seleccionar_preguntas_aleatorias(self, tema, dificultad, cantidad=3):
        """
        Selecciona N preguntas aleatorias de una caja específica

        Args:
            tema: "Derivadas", "Integrales", etc.
            dificultad: "Fácil", "Medio", "Difícil"
            cantidad: número de preguntas a seleccionar (default 3)

        Returns:
            Lista de preguntas seleccionadas
        """
        try:
            # Obtener la caja de preguntas
            caja = self.banco[tema][dificultad]

            # Verificar que haya suficientes preguntas
            if len(caja) < cantidad:
                print(f"ADVERTENCIA: Solo hay {len(caja)} preguntas en {tema}-{dificultad}, se pedían {cantidad}")
                cantidad = len(caja)

            # Seleccionar aleatoriamente SIN repetición
            preguntas_seleccionadas = random.sample(caja, cantidad)

            # Añadir metadata
            for pregunta in preguntas_seleccionadas:
                pregunta['tema'] = tema
                pregunta['dificultad'] = dificultad

            return preguntas_seleccionadas

        except KeyError:
            print(f"ERROR: No existe {tema} o dificultad {dificultad}")
            return []

    def generar_examen_completo(self, temas_config):
            """
            Genera un examen completo con múltiples temas
            """
            examen = []

            for config in temas_config:
                # --- CORRECCIÓN AQUÍ ---
                # 'config' llega desde Kotlin como un LinkedHashMap de Java.
                # No soporta la sintaxis de corchetes ['tema'].
                # Debes usar el método .get() nativo de Java.

                tema = config.get('tema')          # Antes era: config['tema']
                dificultad = config.get('dificultad') # Antes era: config['dificultad']
                # -----------------------

                # Seleccionar 3 preguntas aleatorias de esta caja
                if tema and dificultad: # Pequeña validación extra por seguridad
                    preguntas = self.seleccionar_preguntas_aleatorias(tema, dificultad, cantidad=3)
                    examen.extend(preguntas)

            # Mezclar todas las preguntas del examen
            random.shuffle(examen)

            return examen

    def verificar_respuesta(self, respuesta_usuario, respuesta_correcta, tipo, tolerancia=0.1):
        """
        Verifica si la respuesta del usuario es correcta

        Args:
            respuesta_usuario: lo que escribió el usuario
            respuesta_correcta: la respuesta del banco
            tipo: tipo de pregunta (derivada, integral, etc.)
            tolerancia: margen de error para respuestas numéricas

        Returns:
            True si es correcta, False si no
        """
        try:
            # Limpiar respuestas
            resp_user = respuesta_usuario.strip()
            resp_correct = respuesta_correcta.strip()

            if not resp_user:
                return False

            if tipo in ['derivada', 'integral']:
                # Para expresiones matemáticas: simplificar y comparar
                try:
                    user_expr = sympify(resp_user)
                    correct_expr = sympify(resp_correct)
                    diferencia = simplify(user_expr - correct_expr)
                    return diferencia == 0
                except:
                    # Si falla sympy, comparar strings
                    return resp_user.replace(" ", "") == resp_correct.replace(" ", "")

            elif tipo in ['conversion', 'fisica']:
                # Para números: comparar con tolerancia
                try:
                    num_user = float(resp_user)
                    num_correct = float(resp_correct)
                    return abs(num_user - num_correct) <= tolerancia
                except ValueError:
                    return False

            elif tipo == 'sistema_numerico':
                # Para sistemas numéricos: comparación sin case sensitive
                return resp_user.upper() == resp_correct.upper()

            else:
                # Comparación exacta por defecto
                return resp_user == resp_correct

        except Exception as e:
            print(f"Error al verificar respuesta: {e}")
            return False

    def agregar_pregunta(self, tema, dificultad, pregunta_nueva, guardar_ruta=None):
        """
        Agrega una nueva pregunta al banco (en memoria)

        Args:
            tema: tema de la pregunta
            dificultad: nivel de dificultad
            pregunta_nueva: dict con la pregunta
            guardar_ruta: si se proporciona, guarda el banco actualizado

        Returns:
            True si se agregó exitosamente
        """
        try:
            # Crear tema/dificultad si no existe
            if tema not in self.banco:
                self.banco[tema] = {}
            if dificultad not in self.banco[tema]:
                self.banco[tema][dificultad] = []

            # Generar ID automático
            num_preguntas = len(self.banco[tema][dificultad])
            prefijo = tema[0].upper() + "_" + dificultad[0].upper()
            pregunta_nueva['id'] = f"{prefijo}_{num_preguntas + 1:03d}"

            # Agregar pregunta
            self.banco[tema][dificultad].append(pregunta_nueva)

            # Guardar si se especifica ruta
            if guardar_ruta:
                self._guardar_banco(guardar_ruta)

            return True

        except Exception as e:
            print(f"Error al agregar pregunta: {e}")
            return False

    def _guardar_banco(self, ruta):
        """Guarda el banco de preguntas en el archivo JSON"""
        try:
            with open(ruta, 'w', encoding='utf-8') as f:
                json.dump(self.banco, f, indent=2, ensure_ascii=False)
            return True
        except Exception as e:
            print(f"Error al guardar banco: {e}")
            return False

    def obtener_estadisticas(self):
        """
        Retorna estadísticas del banco de preguntas

        Returns:
            Dict con stats por tema y dificultad
        """
        stats = {}
        total_preguntas = 0

        for tema, dificultades in self.banco.items():
            stats[tema] = {}
            for dificultad, preguntas in dificultades.items():
                num = len(preguntas)
                stats[tema][dificultad] = num
                total_preguntas += num

        stats['TOTAL'] = total_preguntas
        return stats

    def imprimir_estadisticas(self):
        """Imprime un resumen del banco de preguntas"""
        stats = self.obtener_estadisticas()

        print("\n" + "="*50)
        print("BANCO DE PREGUNTAS - ESTADÍSTICAS")
        print("="*50)

        for tema, dificultades in stats.items():
            if tema == 'TOTAL':
                continue
            print(f"\n{tema}:")
            if isinstance(dificultades, dict):
                for dif, num in dificultades.items():
                    print(f"  {dif}: {num} preguntas")

        print(f"\n{'='*50}")
        print(f"TOTAL: {stats['TOTAL']} preguntas")
        print("="*50 + "\n")


# FUNCIÓN DE AYUDA PARA KOTLIN
def crear_generador(ruta_json):
    """
    Función helper para crear el generador desde Kotlin
    """
    return GeneradorPreguntas(ruta_json)