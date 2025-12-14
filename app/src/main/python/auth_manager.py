import json
import hashlib
import os

# --- CLASES Y UTILIDADES DEL CORE ---

class Usuario:
    """Clase simple para representar un usuario con datos de perfil y racha."""
    def __init__(self, nombre, password_hash, es_admin=False,
                 racha_dias=0, foto_url="", cursos_completados=None, stats_examenes=None):

        self.nombre = nombre
        self.password_hash = password_hash
        self.es_admin = es_admin
        self.racha_dias = racha_dias
        self.foto_url = foto_url
        # Guardaremos los cursos como un diccionario: {"modulo_nombre": porcentaje_avance}
        self.cursos_completados = cursos_completados if cursos_completados is not None else {
            "Simbolico": 0,
            "Metodos_Numericos": 0,
            "Conversores": 0
        }
        # Nuevas estadisticas para examenes
        self.stats_examenes = stats_examenes if stats_examenes else {
            "racha_examen": 0,
            "facil_completados": 0,
            "medio_completados": 0,
            "dificil_completados": 0
        }
class AuthManager:
    """
    Gestiona la carga/guardado de usuarios y la lógica de login/registro.
    A diferencia del código original, esta clase no usa tkinter.
    """
    def __init__(self, file_path):
        # self.file_path siempre recibirá la ruta absoluta de Kotlin
        self.file_path = file_path
        self.usuarios = self._cargar_usuarios()
        
    def hash_password(self, password):
        """Genera un hash SHA-256 de la contraseña."""
        return hashlib.sha256(password.encode()).hexdigest()
        
    def _cargar_usuarios(self):
        """Carga los usuarios desde el archivo JSON o crea un admin por defecto."""
        usuarios = {}
        try:
            if os.path.exists(self.file_path):
                with open(self.file_path, 'r') as f:
                    data = json.load(f)
                    for nombre, info in data.items():
                        # Reconstruir objetos Usuario
                        usuarios[nombre] = Usuario(
                            nombre, 
                            info['password_hash'], 
                            info.get('es_admin', False),  # ← COMA AGREGADA
                            info.get('racha_dias', 0),
                            info.get('foto_url', ""),
                            info.get('cursos_completados'),
                            info.get('stats_examenes')
                        )
                    return usuarios
        except Exception:
            # En caso de error de lectura o archivo corrupto, inicializar vacío
            pass

        # Si no hay usuarios válidos, crear el administrador por defecto
        admin_hash = self.hash_password('admin123')
        admin = Usuario('admin', admin_hash, True)
        usuarios['admin'] = admin
        self._guardar_usuarios(usuarios) # Guardar el admin por defecto

        return usuarios

    def _guardar_usuarios(self, usuarios_data=None):
        """Guarda los usuarios actuales en el archivo JSON."""
        if usuarios_data is None:
            usuarios_data = self.usuarios

        data_to_save = {}
        for nombre, usuario in usuarios_data.items():
            data_to_save[nombre] = {
                'password_hash': usuario.password_hash,
                'es_admin': usuario.es_admin,
                'racha_dias': usuario.racha_dias,
                'foto_url': usuario.foto_url,
                'cursos_completados': usuario.cursos_completados,
                'stats_examenes': usuario.stats_examenes
            }
        try:
            with open(self.file_path, 'w') as f:
                json.dump(data_to_save, f, indent=4)
            return True
        except Exception:
            return False


    # --- NUEVAS FUNCIONES DE EDICIÓN ---

    def cambiar_nombre_usuario(self, actual_nombre, nuevo_nombre):
        """Migra los datos de un usuario a una nueva clave (nuevo nombre)."""
        if nuevo_nombre in self.usuarios:
            return False # El nombre ya existe

        if actual_nombre in self.usuarios:
            usuario_obj = self.usuarios[actual_nombre]
            usuario_obj.nombre = nuevo_nombre # Actualizar atributo

            # Mover a nueva clave en el diccionario
            self.usuarios[nuevo_nombre] = usuario_obj
            del self.usuarios[actual_nombre] # Borrar clave vieja

            self._guardar_usuarios()
            return True
        return False

    def cambiar_password(self, usuario, actual_pass, nueva_pass):
        if usuario in self.usuarios:
            pass_hash = self.hash_password(actual_pass)
            if self.usuarios[usuario].password_hash == pass_hash:
                self.usuarios[usuario].password_hash = self.hash_password(nueva_pass)
                self._guardar_usuarios()
                return True
        return False

    def registrar_examen_completado(self, usuario, dificultad):
        """Actualiza las estadísticas de exámenes."""
        if usuario in self.usuarios:
            u = self.usuarios[usuario]
            u.stats_examenes["racha_examen"] += 1

            key = f"{dificultad.lower()}_completados" # facil_completados
            if key in u.stats_examenes:
                u.stats_examenes[key] += 1

            self._guardar_usuarios()
            return True
        return False

    # --- FUNCIONES CLAVE PARA ANDROID ---

    def iniciar_sesion(self, usuario, password):
        """
        Verifica las credenciales de un usuario.
        Retorna True si la sesión es exitosa, False si falla.
        """
        if not usuario or not password:
            return False
            
        if usuario not in self.usuarios:
            # Usuario no encontrado
            return False
            
        password_hash = self.hash_password(password)
        
        if self.usuarios[usuario].password_hash == password_hash:
            # Login exitoso
            return True
        else:
            # Contraseña incorrecta
            return False

    def registrar_usuario(self, usuario, password):
        """
        Registra un nuevo usuario si no existe.
        Retorna:
        - True: Registro exitoso.
        - False: El usuario ya existe o la entrada no es válida.
        """
        usuario = usuario.strip()
        password = password.strip()
        
        if not usuario or not password:
            return False
            
        if usuario in self.usuarios:
            # El usuario ya existe
            return False
            
        # Crear y guardar nuevo usuario
        password_hash = self.hash_password(password)
        nuevo_usuario = Usuario(usuario, password_hash)
        self.usuarios[usuario] = nuevo_usuario
        self._guardar_usuarios()
        
        return True

    def obtener_datos_perfil(self, nombre_usuario):
        """
        Retorna los datos del perfil del usuario como un diccionario.
        """
        if nombre_usuario in self.usuarios:
            usuario = self.usuarios[nombre_usuario]
            return {
                "nombre": usuario.nombre,
                "racha_dias": usuario.racha_dias,
                "foto_url": usuario.foto_url,
                "cursos_completados": usuario.cursos_completados,
                "stats_examenes": usuario.stats_examenes
            }
        return None

    def actualizar_foto_perfil(self, nombre_usuario, nueva_url):
        """
        Actualiza la URL de la foto de perfil y guarda el JSON.
        """
        if nombre_usuario in self.usuarios:
            self.usuarios[nombre_usuario].foto_url = nueva_url
            self._guardar_usuarios()
            return True
        return False