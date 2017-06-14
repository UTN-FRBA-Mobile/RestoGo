package ar.com.utn.restogo;


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
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ar.com.utn.restogo.modelo.TipoComida;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

public class PublicarFragment extends Fragment {

    private static final int RC_SELECC_UBIC = 9002;
    private static final int RC_SACAR_FOTO = 9003;
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
            if (photoFile != null) {
                // El authorities (2do param) depende del FileProvider del manifest
                Uri photoURI = FileProvider.getUriForFile(getActivity(), "ar.com.utn.restogo.fileprovider", photoFile);
                grantPermissionsToUri(getActivity(), intent, photoURI);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, RC_SACAR_FOTO);
            }
        }
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
                    Place place = PlaceAutocomplete.getPlace(getActivity(), data);
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




