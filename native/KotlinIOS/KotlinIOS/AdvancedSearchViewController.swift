import UIKit
import SharedCode


class AdvancedSearchViewController: UIViewController {

    var delegate: AdvancedSearchDelegate?
    
    @IBOutlet weak var applyButton: UIButton!
    @IBOutlet weak var dismissButton: UIButton!
    @IBOutlet weak var numberAdults: UILabel!
    @IBOutlet weak var numberChildren: UILabel!
    @IBOutlet weak var adultsStepper: UIStepper!
    @IBOutlet weak var childrenStepper: UIStepper!
    @IBOutlet weak var datePicker: UIDatePicker!
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    @IBAction func didTapDismiss(_ sender: Any) {
        dismiss(animated: true)
    }
    @IBAction func didTapApply(_ sender: Any) {
        
        delegate?.applyButtonPressed(numAdults: Int(adultsStepper.value), numChildren: Int(childrenStepper.value), date: datePicker.date)
        dismiss(animated: true)
    }
    @IBAction func incrementAdults(_ sender: Any) {
        numberAdults.text = String(round(adultsStepper.value))
        
    }
    @IBAction func incrementChildren(_ sender: Any) {
        numberChildren.text = String(round(childrenStepper.value))
    }
}

protocol AdvancedSearchDelegate {
    func applyButtonPressed(numAdults: Int, numChildren: Int, date: Date)
}
