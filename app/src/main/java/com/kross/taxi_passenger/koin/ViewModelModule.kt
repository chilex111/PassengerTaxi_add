package com.kross.taxi_passenger.koin

import com.kross.taxi_passenger.domain.*
import com.kross.taxi_passenger.domain.SupportViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.module

val viewModelModule: Module = module {
    viewModel { ExampleViewModel(get(), get()) }
    viewModel { LoginViewModel(get(), get()) }
    viewModel { RegistrationViewModel(get(), get()) }
    viewModel { AddCardViewModel(get(), get()) }
    viewModel { AuthorizationViewModel(get(), get()) }
    viewModel { CabinetViewModel(get(), get()) }
    viewModel { CarOwnerRegistrationViewModel(get(), get()) }
    viewModel { MapViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { WaypointsViewModel(get()) }
    viewModel { AddPointViewModel(get(), get()) }
    viewModel { MyCardsViewModel(get(), get()) }
    viewModel { FavoriteViewModel(get(), get(), get(),get()) }
    viewModel { CancelTripViewModel(get(), get(), get()) }
    viewModel { ChooseCarMakeViewModel(get(), get()) }
    viewModel { CameraViewModel(get()) }
    viewModel { CarsListViewModel(get(), get()) }
    viewModel { AddCarViewModel(get(),get()) }
    viewModel { CarInfoViewModel(get(),get()) }

    viewModel { AboutActivityViewModel(get(),get()) }
    viewModel { NewsViewModel(get(),get()) }
    viewModel { FAQViewModel(get(),get()) }
    viewModel { SupportViewModel(get(), get()) }
    viewModel { EditProfileViewModel(get(),get()) }

    viewModel { DriversListViewModel(get(),get()) }
    viewModel { DriverDetailsViewModel(get(),get()) }
    viewModel { AllTripsViewModel(get(),get()) }

}
