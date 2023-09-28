package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.newOutletPage.newOutletMap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.databinding.MapFragmentBinding
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.plugin.animation.CameraAnimatorOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager

class MapFragment : Fragment(R.layout.map_fragment) {

    private val binding by viewBinding<MapFragmentBinding>()
    private val args by navArgs<MapFragmentArgs>()
    private lateinit var mapboxMap: MapboxMap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapboxMap = binding.mapView.getMapboxMap()
        mapboxMap.setCamera(cameraOptions {
            center(Point.fromLngLat(args.longitude.toDouble(), args.latitude.toDouble()))
            zoom(9.0)
        })

        addAnnotationToMap()
        showAnimation()
    }

    private fun addAnnotationToMap() {

        bitmapFromDrawableRes(
            requireContext(), R.drawable.ic_coordinate_blue
        )?.let {

            val lng = args.longitude.toDouble()
            val lat = args.latitude.toDouble()

            val annotationApi = binding.mapView.annotations
            val pointAnnotationManager = annotationApi.createPointAnnotationManager()

            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(lng, lat))
                .withIconImage(it)

            pointAnnotationManager.create(pointAnnotationOptions)
        }
    }

    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }

    private fun showAnimation() {
        mapboxMap.loadStyleUri(
            Style.MAPBOX_STREETS
        ) {
            binding.mapView.camera.apply {
                val bearing =
                    createBearingAnimator(CameraAnimatorOptions.cameraAnimatorOptions(-45.0)) {
                        duration = 4000
                        interpolator = AccelerateDecelerateInterpolator()
                    }
                val zoom = createZoomAnimator(
                    CameraAnimatorOptions.cameraAnimatorOptions(16.0) {
                        startValue(9.0)
                    }
                ) {
                    duration = 4000
                    interpolator = AccelerateDecelerateInterpolator()
                }
                val pitch = createPitchAnimator(
                    CameraAnimatorOptions.cameraAnimatorOptions(55.0) {
                        startValue(0.0)
                    }
                ) {
                    duration = 4000
                    interpolator = AccelerateDecelerateInterpolator()
                }
                playAnimatorsSequentially(zoom, pitch, bearing)
            }
        }
    }
}