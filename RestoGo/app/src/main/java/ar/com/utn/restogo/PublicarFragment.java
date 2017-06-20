package ar.com.utn.restogo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ar.com.utn.restogo.modelo.Restaurante;
import ar.com.utn.restogo.modelo.TipoComida;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

public class PublicarFragment extends Fragment {

    private static final int RC_SELECC_UBIC = 9002;
    private static final int RC_SACAR_FOTO = 9003;
    private static final int RC_PERMISO_LECTURA = 9005;
    private static final int RC_SELECC_FOTO = 9006;

    public static final String SELECTED_PICTURE = "selectedImage";

    private static final String TAG = "PublicarFragment";

    @BindView(R.id.txtNombre) TextView txtNombre;
    @BindView(R.id.txtDireccion) TextView txtDireccion;
    @BindView(R.id.btnSeleccUbicac) Button btnSeleccUbicac;
    @BindView(R.id.txtTiposComida) TextView txtTiposComida;
    @BindView(R.id.btnSeleccTiposComida) Button btnSeleccTiposComida;
    @BindView(R.id.txtHoraApertura) TextView txtHoraApertura;
    @BindView(R.id.txtHoraCierre) TextView txtHoraCierre;
    @BindView(R.id.imageView) ImageView imageView;
    @BindView(R.id.btnCamara) Button btnCamara;
    private Unbinder unbinder;

    String currentPhotoPath;
    private Place place;

    public PublicarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_publicar, container, false);
        unbinder = ButterKnife.bind(this, view);

        txtDireccion.setEnabled(false);
        txtTiposComida.setEnabled(false);
        btnCamara.setEnabled(getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA));

        currentPhotoPath = getActivity().getIntent().getStringExtra(SELECTED_PICTURE);
        loadImage();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * Muestra un dialog con checkboxes para seleccionar tipos de comida disponibles
     */
    @OnClick(R.id.btnSeleccTiposComida)
    void mostrarDialogTiposComidas() {
        AlertDialog dialog;

        final CharSequence[] disponibles = new CharSequence[TipoComida.values().length];
        final ArrayList<TipoComida> seleccs = new ArrayList();

        for (int n = 0; n < TipoComida.values().length; n++) {
            disponibles[n] = TipoComida.values()[n].toString();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.dialog_selecc_comidas));
        builder.setMultiChoiceItems(disponibles, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    // indexSelected contains the index of item (of which checkbox checked)
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected,
                                        boolean isChecked) {
                        TipoComida selecc = TipoComida.values()[indexSelected];

                        if (isChecked) {
                            seleccs.add(selecc);
                        } else if (seleccs.contains(selecc)) {
                            seleccs.remove(selecc);
                        }
                    }
                })
                .setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String stringTipos = "";
                        for (TipoComida tipo : seleccs) {
                            stringTipos = stringTipos + " ," + tipo.toString();
                        }
                        txtTiposComida.setText(stringTipos);
                    }
                })
                .setNegativeButton(getString(R.string.btn_cancelar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        dialog = builder.create();
        dialog.show();
    }

    /**
     * Lanza un intent de Google Places para buscar una direccion
     */
    @OnClick(R.id.btnSeleccUbicac)
    void seleccUbicacion() {
        try {
            // Filtro limitado a direcciones de Argentina
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                    .setCountry("ARG")
                    .build();
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .setFilter(typeFilter)
                    .build(getActivity());
            startActivityForResult(intent, RC_SELECC_UBIC);
        } catch (GooglePlayServicesRepairableException e) {
            Log.d(TAG, "Google Places: GooglePlayServicesRepairableException");
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.d(TAG, "Google Places: GooglePlayServicesNotAvailableException");
            e.printStackTrace();
        }
    }

    /**
     * Abre un timepicker para la hora de apertura
     */
    @OnClick(R.id.btnApertura)
    void seleccHoraApertura() {
        final Calendar c = Calendar.getInstance();
        new TimePickerDialog(getActivity(), timePickerAperturaListener,
                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }

    /**
     * Abre un timepicker para la hora de cierre
     */
    @OnClick(R.id.btnCierre)
    void seleccHoraCierre() {
        final Calendar c = Calendar.getInstance();
        new TimePickerDialog(getActivity(), timePickerCierreListener,
                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }

    @OnClick(R.id.btnCamara)
    void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Crea el archivo donde guardar la foto
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d(TAG, "Error al crear el archivo para la foto");
            }
            // Lanza el intent solo si pudo crear el archivo
            if (photoFile != null) {
                // El authorities (2do param) depende del FileProvider del manifest
                Uri photoURI = FileProvider.getUriForFile(getActivity(), "ar.com.utn.restogo.fileprovider", photoFile);
                grantPermissionsToUri(getActivity(), intent, photoURI);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, RC_SACAR_FOTO);
            }
        }
    }

    @OnClick(R.id.btnGaleria)
    void abrirGaleria() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(getContext(), R.string.permiso_lectura_almac, Toast.LENGTH_LONG).show();
            }
            else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE }, RC_PERMISO_LECTURA);
            }
            return;
        }
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        gallery.setType("image/*");
        startActivityForResult(gallery, RC_SELECC_FOTO);
    }

    @OnClick(R.id.btnPublicar)
    void publicar() {
        boolean cancelar = false;
        View campoConError = null;

        txtNombre.setError(null);
        txtDireccion.setError(null);

        String descripcion = txtNombre.getText().toString();
        String direccion = txtDireccion.getText().toString();
        String horaApertura = txtHoraApertura.getText().toString();
        String horaCierre = txtHoraCierre.getText().toString();

        if (TextUtils.isEmpty(horaCierre)) {
            txtHoraCierre.setError(getString(R.string.error_campo_requerido));
            campoConError = txtHoraCierre;
            cancelar = true;
        }
        if (TextUtils.isEmpty(horaApertura)) {
            txtHoraApertura.setError(getString(R.string.error_campo_requerido));
            campoConError = txtHoraApertura;
            cancelar = true;
        }
        if (TextUtils.isEmpty(direccion)) {
            txtDireccion.setError(getString(R.string.error_campo_requerido));
            campoConError = txtDireccion;
            cancelar = true;
        }
        if (TextUtils.isEmpty(descripcion)) {
            txtNombre.setError(getString(R.string.error_campo_requerido));
            campoConError = txtNombre;
            cancelar = true;
        }

        if (cancelar) {
            campoConError.requestFocus();
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        Restaurante restaurante = new Restaurante();
        restaurante.setDescripcion(descripcion);
        restaurante.setDireccion(direccion);
        restaurante.setLatitute(place.getLatLng().latitude);
        restaurante.setLongitute(place.getLatLng().longitude);
        restaurante.setHoraApertura(horaApertura);
        restaurante.setHoraCierre(horaCierre);
        // TODO set img, dias, tipos de comida

        database.getReference("restaurantes").push().setValue(restaurante);
    }

    /**
     * Crea un archivo donde guardar la foto sacada
     * @return archivo
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void loadImage() {
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, null);
        imageView.setImageBitmap(bitmap);
    }

    private void saveImage(Uri data) {
        try {
            InputStream input = getActivity().getContentResolver().openInputStream(data);
            assert input != null;
            File file = createImageFile();
            FileOutputStream output = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len = input.read(buffer);
            while (len != -1) {
                output.write(buffer, 0, len);
                len = input.read(buffer);
            };
            output.close();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void grantPermissionsToUri(Context context, Intent intent, Uri uri) {
        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_SELECC_UBIC:
                if (resultCode == RESULT_OK) {
                    place = PlaceAutocomplete.getPlace(getActivity(), data);
                    txtDireccion.setText(place.getName());
                }
                break;
            case RC_SACAR_FOTO:
                if (resultCode == RESULT_OK) {
                    Intent resultData = new Intent();
                    resultData.putExtra(SELECTED_PICTURE, currentPhotoPath);
                    getActivity().setResult(RESULT_OK, resultData);
                    loadImage();
                }
                else {
                    new File(currentPhotoPath).delete();
                }
                break;
            case RC_SELECC_FOTO:
                if (resultCode == RESULT_OK && data != null) {
                    saveImage(data.getData());
                    Intent resultData = new Intent();
                    resultData.putExtra(SELECTED_PICTURE, currentPhotoPath);
                    getActivity().setResult(RESULT_OK, resultData);
                    loadImage();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case RC_PERMISO_LECTURA:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    abrirGaleria();
                }
                break;
            default:
                break;
        }
    }

    // Listener para el time picker de la hora de apertura
    private TimePickerDialog.OnTimeSetListener timePickerAperturaListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
            txtHoraApertura.setText(new StringBuilder().append(timeFormatString(selectedHour))
                    .append(":").append(timeFormatString(selectedMinute)));
        }
    };

    // Listener para el time picker de la hora de cierre
    private TimePickerDialog.OnTimeSetListener timePickerCierreListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
            txtHoraCierre.setText(new StringBuilder().append(timeFormatString(selectedHour))
                    .append(":").append(timeFormatString(selectedMinute)));
        }
    };

    /**
     * Cuando la hora/minuto es menor a 10, le agrega adelante un "0" para mostrar bien el string
     * @param c
     * @return
     */
    private static String timeFormatString(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
}




