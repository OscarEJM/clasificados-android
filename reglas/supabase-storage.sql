-- Reglas (políticas) de Supabase Storage de Clasificados para el bucket «anuncios-fotos»
-- (HU-14 y HU-16 · Reglas de seguridad de los datos; RNF-15, RNF-16).
-- Esta es la copia que se versiona; las que rigen son las del proyecto «clasificados» de Supabase.
--
-- Supabase reconoce a los usuarios por el pase (token) de Firebase, configurado como proveedor de
-- autenticación de terceros (HU-02). Firebase no incluye el rol «authenticated» sin funciones de
-- pago (RN-19), así que cada regla revisa que el pase lo haya emitido el proyecto de Firebase
-- clasificados-d5227 (campos «iss» y «aud»). La carpeta de cada usuario es su uid (campo «sub»).

-- El bucket es privado: sin sesión no se ve ninguna foto (HU-16).
update storage.buckets set public = false where id = 'anuncios-fotos';

-- Ver: cualquier usuario con sesión ve todas las fotos (HU-16).
drop policy if exists "Clasificados ver fotos propias" on storage.objects;
create policy "Clasificados ver fotos con sesion" on storage.objects for select to public using (
  bucket_id = 'anuncios-fotos'
  and auth.jwt()->>'iss' = 'https://securetoken.google.com/clasificados-d5227'
  and auth.jwt()->>'aud' = 'clasificados-d5227'
);

-- Subir: solo dentro de la carpeta propia (HU-14).
create policy "Clasificados subir fotos propias" on storage.objects for insert to public with check (
  bucket_id = 'anuncios-fotos'
  and auth.jwt()->>'iss' = 'https://securetoken.google.com/clasificados-d5227'
  and auth.jwt()->>'aud' = 'clasificados-d5227'
  and (storage.foldername(name))[1] = auth.jwt()->>'sub'
);

-- Borrar: solo dentro de la carpeta propia (HU-14, RNF-16).
create policy "Clasificados borrar fotos propias" on storage.objects for delete to public using (
  bucket_id = 'anuncios-fotos'
  and auth.jwt()->>'iss' = 'https://securetoken.google.com/clasificados-d5227'
  and auth.jwt()->>'aud' = 'clasificados-d5227'
  and (storage.foldername(name))[1] = auth.jwt()->>'sub'
);
