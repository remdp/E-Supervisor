package com.euromix.esupervisor.screens

//class ViewModelFactory(
//    private val app: App
//): ViewModelProvider.Factory{
//
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        val viewModel = when(modelClass){
//            DocsEmixListViewModel::class.java -> {
//                DocsEmixListViewModel(app.docsEmixService)
//            }
//            DocEmixDetailsViewModel::class.java -> {
//                DocEmixDetailsViewModel(app.docsEmixService)
//            }
//            else -> {
//                throw IllegalStateException("Unknown view model class")
//            }
//        }
//
//        return viewModel as T
//    }
//}
//
//
//fun Fragment.factory() = ViewModelFactory(requireContext().applicationContext as App)
//
//fun Fragment.navigator() = requireActivity() as Navigator