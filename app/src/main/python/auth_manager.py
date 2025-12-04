import json
import hashlib
import os

# --- CLASES Y UTILIDADES DEL CORE ---

class Usuario:
    """Clase simple para representar un usuario, solo con datos esenciales para auth."""
    def __init__(self, nombre, password_hash, es_admin=False):
        self.nombre = nombre
        self.password_hash = password_hash
        self.es_admin = es_admin

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
                            info.get('es_admin', False)
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
                'es_admin': usuario.es_admin
            }
        try:
            with open(self.file_path, 'w') as f:
                json.dump(data_to_save, f, indent=4)
            return True
        except Exception:
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
